It splits up a single ***.yaml** file into multiple parts. The files holding the lower level information are placed in a structure of subdirectories. It works interactively, you get a GUI that lets you control what goes where.

#### How do I build it?
You can build in the usual way with Maven:

	mvn clean package

The pom is set up to create a "fat" jar, meaning that it will include all the necessary Java libraries in the artifact.

Note: the utility was developed using Java 8. It also compiles and runs in Java 11.

#### How do I run it?
You can start the application from the command line with 

	  java -jar splitify.jar

**splitify.jar** is the fat jar. It lives in the target directory. 

This opens up the main window from where you can load an input file and write output files. Alternatively you can place the name of the input file on the command line:

	  java -jar splitify.jar input.yaml

This will load the input file at program startup.

#### The text is too small to read, what gives?
Java Swing and Windows do not work very well together in some cases, see for example

   https://superuser.com/questions/652606/is-there-a-way-to-increase-the-default-font-size-for-java-gui-applications

The basic problem seems to be that Swing *thinks* it is using point size, but Windows *thinks* it is using pixel size.
In particular on high res displays that are not very large this may lead to unreadable small font size.

You can tell Swing to use specific fonts and sizes by adding VM args to the command line:

    java -Dswing.aatext=true -Dswing.plaf.metal.controlFont=Tahoma-plain-18 -Dswing.plaf.metal.userFont=Tahoma-plain-18 -jar  splitify.jar

This will change the font to Tahoma and the size of text in menus and other screen element to 18 (point? pixel? I would not know.) If the text in the preview pane is too small you'll have to change its size in the source code, in 

     TreeGui.makePreview()


#### How does it work?
The tree in the left pane shows a schematic representation of the contents of the input file. When you select a node in this tree the preview pane at the  right will show the content as it would be written to file.

You can influence the content with the checkboxes in the option pane above the text of the preview, ald also via two dialogs that can be launched from the settings menu.

The checkbox **childnodes in separate files** controls wether the childnodes for this node will be included in the file for the current node or not. If not, each child will be placed in its own, separate file and a directory for it (and its descendants) will
be created when the childnode is saved. By toggling this checkbox you can see the content in the preview pane change immediately.

The editable field for the file **path segment** name lets you change the name of the subdirectory where the childnodes will be saved. This value does not influence the content of the files, it is meant to let you control the names of the files and directories.

The above options are retained separately for each node in the tree, but they are not saved on program exit. However, the program provides sensible defaults which are set when an input file is loaded:

- Adding a definitions/config wrapper is set when the input file has such a wrapper.
- Splitting up children is set for nodes at the first and second level  in the tree.
- The directory name is derived from the node path by stripping all text before a colon (if present). If the node path has multiple segments, this is done separately for each segment.

In addition, there is a settings wizard dialog that lets you set the value for this checkbox automatically based on criteria that you can select.

#### How does the Node Settings Wizard work?
This dialog pops up from the *Settings->Node Settings Wizard...*"* menu option. It lets you set the *childnodes in separate files* checkbox for all nodes at once.

It presents two different ways of selecting which nodes will have their checkbox set. 

By selecting the *stop at depth* radio button and setting a value in the dropdown, all nodes up to but not including that depth in the structure will have the checkbox set. All nodes lower in the structure will have the checkbox cleared. The list of node depths you can choose from is hardocded and runs from 2 to 8. While  loading a fresh input file, this setting is applied automatically with a default value of 2.

Similarly, selecting the *stop at node type* radio button will set all checkboxes of all nodes up to but not including the first encountered instance of the selected node type in the path starting at the root. The node types which you can select in this case are loaded dynamically from Yaml file, the dropdown contains a list of all primary node types present.

So if you for instance select the *hippo:handle* node type, all your content will be split up into one document per file, with each file containing all three versions of the document (draft, unpublished, published).

To actually apply the settings in this dialog to all nodes, use the **do it** button. This will first clear all checkboxes of all nodes, and then set the checkboxes of the nodes as indicated by the controls in this dialog starting at the root node and working downwards recursively.

Note that the dialog is modal, you can only see changes in the preview once it has closed again.

#### What do the output transformations do?
The checkbox **add definitions/config nodes** controls wether  the content will be wrapped in a

	    definitions:
	      config:

structure or not. The effect of this setting is shown in the preview after closing the dialog. There is some logic that suppresses the efect of this setting for nodes that will be included in the output file of on of their ancestor nodes. The wrapper is only shown for nodes that will actually have their own output file.

Note that the initial value for this checkbox is set dynamically during the loading and analysis of the input file.

The other three checkboxes are present for exeptional cases. The content of the nodes is kept internally in a structure of JSON nodes in memory and rendered using the ObjectMapper. Unfortunately, the output as delivered by ObjectMapper does not comply perfectly with the YAML syntax as expected by Bloomreach. So there is some postprocessing going on in an attempt to hammer the square pegs into the triangular holes.

Still more unfortunately, such hammering sometimes also affects the actual content of the nodes by removing quotes and other unwanted changes.

By manipulating these checkboxes you can suppress some of the postprocessing and hope that this will leave you with a usable splitup file.


#### How and where is the output saved?
There are two **save**  buttons. You can choose to save only the file for the node that is currently selected (the content of the preview pane will be written to the file path shown in the options pane).
Or you can choose to save everything. In that case the save operation is performed for all nodes, starting at the root and descending recursively as indicated by the "separate children" checkbox for each node.

The directory structure for the output is created in the same directory that holds the input file, so that the input file is effectively split up in place. However, due to the coupling between the node path of the output and the location in the subdirectory structure, you will probably get a bunch of empty subdirectories before the actual output files are written out.

#### Can I  integrate it in IntelliJ?
Yes. Choose **File->settings->Tools->External Tools**, then use the **+** to add a new tool definition. In the dialog, enter these settings:
- **Name** splitify
- **Description** Split YAML
- **Program** /usr/bin/java   or on widows the path to your java executable.
- **Arguments** -jar /opt/splitify.jar  $FilePath$.  The full path to the jar file containing the tool is the second command line argument (after **-jar**) and the name of the file you want to split is added as an IntelliJ macro.

To use the tool from IntelliJ, open the *.yaml file you want to split in the IntelliJ editor, then invoke the tool with **Tools->External Tools->splitify**
