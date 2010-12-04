package org.mirah.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import org.mirah.MirahCommand;

/**
 * Compiles Mirah source files
 *
 * @goal compile
 * @phase compile
 * @threadSafe
 * @requiresDependencyResolution compile
 */
public class MirahCompilerMojo extends AbstractMojo {
    /**
     * Project classpath.
     *
     * @parameter default-value="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> classpathElements;
    /**
     * The source directories containing the sources to be compiled.
     *
     * @parameter default-value="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;
    /**
     * Classes destination directory
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File outputDirectory;
    /**
     * Classes source directory
     * @parameter expression="src/main/mirah"
     */
    private File sourceDirectory;
    /**
     * Whether produce bytecode or java source
     * @parameter bytecode, default true
     */
    private boolean bytecode = true;
    /**
     * Show log
     * @parameter verbose, default false
     */
    private boolean verbose;

    public void execute() throws MojoExecutionException {
       if (bytecode) {
          executeMirahCompiler(outputDirectory.getAbsolutePath());
       } else {
          String javaSourceRoot = compileSourceRoots.get(0);
          executeMirahCompiler(javaSourceRoot);
       }
    }

    private void executeMirahCompiler(String output) throws MojoExecutionException {
       if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        List arguments = new ArrayList();
        if (!bytecode)
          arguments.add("--java");
        if (verbose)
          arguments.add("-V");

        arguments.add("-d");
        arguments.add(output);

        try {
            arguments.add("-c");
            arguments.add(StringUtils.join(classpathElements.iterator(), File.pathSeparator));

            arguments.add(sourceDirectory.getAbsolutePath());

            MirahCommand.compile(arguments);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
