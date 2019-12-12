#### What does it do?
It splits up a single ***.yaml** file into multiple parts. The files holding the lower level information are placed in a structure of subdirectories.

#### How do I start it?
You can start the application from the command line with 

	  java -jar splitify.jar

(the name of the actual jar file may be different due to information that Maven adds to the filename). This opens up the main window from where you can load an input file and write output files.

Alternatively you can place the name of the input file on the command line:

	  java -jar splitify.jar input.yaml

This will load the input file at program startup.

Note: the app was compiled for Java 8. You can build in the usual way with Maven:

	mvn clean package

Maven is set up to create a "fat" jar, meaning that it will inculde all the necessary Java libraries in the artifact.

#### How does it work?
The tree in the left pane shows a schematic representation of the contents of the input file. When you select a node in this tree the preview pane at the  right will show the content as it would be written to file.

You can influence the content with the checkboxes in the option pane above th text of the preview.

The checkbox **add definitions/config nodes** controls wether  the content will be wrapped in a

	    definitions:
	      config:

structure or not. You can see the effect immediately in the preview.

The checkbox "childnodes in separate files" controls wether the childnodes for this node will be included in the file for the current node or not. If not, each child will be placed in its own, separate file and a directory for it (and its descendants) will
be created when the childnode is saved. By toggling this checkbox you can see the content in the preview pane change immediately.

The editable field for the file path segment name lets you change the name of the subdirectory where the childnodes will be saved. This value does not influence the content on the files, it is meant to let you control the names of the files and directories.

The above options are retained separately for each node in the tree, but they are not saved on program exit. However, the program provides sensible defaults which are set when an input file is loaded:

- Adding a definitions/config wrapper is set for all nodes when the input file has such a wrapper.
- Splitting up children is set for nodes at the first and second level  in the tree.
- The directory name is derived from the node path by stripping all text before a colon (if present). If the node path has multiple segments, this is done separately for each segment.

#### How and where is the output saved?
There are two **save**  buttons. You can choose to save only the file for the node that is currently selected (the content of the preview pane will be written to the file path shown in the options pane).
Or you can choose to save everything. In that case the save operation is performed for all nodes, starting at the root and descending recursively as indicated by the "separate children" checkbox for each node.

The directory structure for the output is created in the same directory that holds the input file, so that the input file is effectively split up in place.

#### Can I  integrate it in IntelliJ?
Yes. Choose **File->settings->Tools->External Tools**, then use the **+** to add a new tool definition. In the dialog, enter these settings:
- **Name** splitify
- **Description** Split YAML
- **Program** /usr/bin/java   or on widows the path to your java executable.
- **Arguments** -jar /opt/splitify.jar  $FilePath$.  The full path to the jar file containing the tool is the second command line argument (after **-jar**) and the name of the file you want to split is added as an IntelliJ macro.

To use the tool from IntelliJ, open the *.yaml file you want to split in the IntelliJ editor, then invoke the tool with **Tools->External Tools->splitify**
