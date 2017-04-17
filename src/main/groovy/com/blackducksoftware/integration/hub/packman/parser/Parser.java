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
package com.blackducksoftware.integration.hub.packman.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter;
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer;
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.packman.PackageManager;
import com.blackducksoftware.integration.hub.packman.search.PackageManagerSearcher;
import com.google.gson.Gson;

@Component
public class Parser {
    @Autowired
    private List<PackageManagerSearcher> packageManagerSearchers;

    @Autowired
    private Gson gson;

    @Autowired
    private DependencyNodeTransformer dependencyNodeTransformer;

    @PostConstruct
    public void init() throws IOException {
        final String[] sourcePaths = new String[] { "/Users/jmathews/ruby/black-duck-swift-sample" };
        final String outputDirectoryPath = "";

        for (final PackageManagerSearcher packageManagerSearcher : packageManagerSearchers) {
            for (final String sourcePath : sourcePaths) {
                if (packageManagerSearcher.isPackageManagerApplicable(sourcePath)) {
                    final List<DependencyNode> projectNodes = packageManagerSearcher.extractDependencyNodes(sourcePath);
                    if (projectNodes != null && projectNodes.size() > 0) {
                        createOutput(outputDirectoryPath, packageManagerSearcher.getPackageManager(), projectNodes);
                    }
                }
            }
        }
    }

    private void createOutput(final String outputDirectoryPath, final PackageManager packageManager, final List<DependencyNode> projectNodes) {
        final File outputDirectory = new File(outputDirectoryPath);
        outputDirectory.mkdirs();

        for (final DependencyNode project : projectNodes) {
            final String filename = String.format("%s_%s_%s_bdio.jsonld", packageManager.toString(), project.name, project.version);
            try (final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(filename))) {
                final SimpleBdioDocument bdioDocument = dependencyNodeTransformer.transformDependencyNode(project);
                bdioWriter.writeSimpleBdioDocument(bdioDocument);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}