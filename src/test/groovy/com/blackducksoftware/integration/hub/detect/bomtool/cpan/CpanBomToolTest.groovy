/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.PathExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.CpanBomTool
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.BdioCreationUtil

@Ignore
class CpanBomToolTest {
    private final CpanPackager cpanPackager = new CpanPackager()
    private final BdioCreationUtil bdioCreationUtil = new BdioCreationUtil()

    private final String sourcePath = '~/Downloads/grcpan'
    private final String cpanListOutputPath = "${sourcePath}/cpan-l-out.txt"
    private final String cpanmShowDepsOutputPath = "${sourcePath}/cpanm-showdeps-out.txt"
    private final String outputFilePath = "${sourcePath}/testOutput.jsonld"

    @Before
    public void init() {
        cpanPackager.cpanListParser = new CpanListParser()
        cpanPackager.nameVersionNodeTransformer = new NameVersionNodeTransformer()
    }

    @Test
    public void makeDependencyNodesExternalTest() {
        final String cpanListText = new File(cpanListOutputPath).text
        final String showDepsText = new File(cpanmShowDepsOutputPath).text

        Set<DependencyNode> dependencyNodes = cpanPackager.makeDependencyNodes(cpanListText, showDepsText)
        println dependencyNodes

        ExternalId externalId = new PathExternalId(CpanBomTool.CPAN_FORGE, sourcePath)
        def detectCodeLocation = new DetectCodeLocation(BomToolType.CPAN, sourcePath, 'testBdio', 'output', externalId, dependencyNodes)

        bdioCreationUtil.createBdioDocument(new File(outputFilePath), detectCodeLocation)
    }
}
