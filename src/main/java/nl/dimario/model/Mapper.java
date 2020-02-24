package nl.dimario.model;
/**
 *     This file is part of Splitify
 *
 *     Splitify is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, version 3 of the License.
 *
 *     Splitify is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class Mapper {

    private static ObjectMapper objectMapper;

    public static ObjectMapper getMapper() {
        if (objectMapper == null) {

            YAMLFactory yamlFactory = new YAMLFactory();
            yamlFactory.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
            yamlFactory.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
            objectMapper = new ObjectMapper(yamlFactory);
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        return objectMapper;
    }
}
