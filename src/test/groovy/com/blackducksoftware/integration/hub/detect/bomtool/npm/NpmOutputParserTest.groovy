package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.google.gson.Gson

public class NpmOutputParserTest {

    @Test
    public void npmCliDependencyFinder() throws IOException {
        def parser = new NpmCliDependencyFinder()
        def testIn = new File(getClass().getResource("/npm/packman_proj_dependencies.json").getFile())

        parser.setGson(new Gson())
        parser.setNodeTransformer(new NameVersionNodeTransformer())

        DependencyNode node = parser.convertNpmJsonFileToDependencyNode(testIn)
        def testOut = new File(getClass().getResource("/npm/npmParseOutput.txt").getFile())

        assertTrue(node.toString().contentEquals(testOut.text))
    }
}
