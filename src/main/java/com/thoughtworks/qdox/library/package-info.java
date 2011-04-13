/**
 * Provides classes to construct a classloader-like structure of libraries
 * 
 * There are two types of ClassLibraryBuilders:
 * <ul>
 *   <li><code>SortedClassLibraryBuilder</code>, which bundles the libraries by type</li>
 *   <li><code>OrderedClassLibraryBuilder</code>, which respects the order of added libraries</li>
 * </ul>
 *   
 * There are four types of ClassLibraries:
 * <ul>
 *   <li><code>ClassLoaderLibrary</code>, which uses the classpath to search for sources or binaries</li>
 *   <li><code>SourceLibrary</code>, which uses a specific sourcefile</li>
 *   <li><code>SourceFolderLibrary</code>, which tries to locate sourcefiles by translating the package to folders</li>
 *   <li><code>ClassNameLibrary</code>, which generates an anonymous class, as if it would exist</li>
 * </ul>
 *   
 */
package com.thoughtworks.qdox.library;