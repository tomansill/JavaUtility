package com.ansill.utility;

import com.ansill.validation.Validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.ansill.utility.Utility.f;
import static com.ansill.utility.Utility.simpleToString;

/**
 * Utility class related to resources
 */
public final class ResourceUtility{

  /**
   * Private constructor
   * <p>
   * No instantiations allowed because this is an utility class
   *
   * @throws AssertionError thrown if any instantiations were attempted
   */
  private ResourceUtility(){
    throw new AssertionError(f("No {} instances for you!", this.getClass().getName()));
  }

  /**
   * Returns the listing of resource filepath in resources area, ignoring directories
   * <B>NOTE:</B> This is more reliable than just calling <i>getResource(String)</i> because in JAR files,
   * <i>getResource(String)</i> returns directories as a single file and it will fail. In this method, we detect
   * whether if we're loading from resources in a standard filesystem or in a JAR file. If it is in standard
   * filesystem, then it scans the files normally with File, but if it's a JAR, then we need to load it using JarFile
   * and scan entries, then parse filepaths to get what we're looking for.
   *
   * @param clazz     class
   * @param path      path of resource directory
   * @param recursive true to search for files in directories
   * @return optional object that may contain the resource listing, if path doesn't exist, then object will be empty
   * @throws URISyntaxException thrown if there's an issue with URL syntax
   * @throws IOException        thrown if there's an issue with reading the JarFile
   */
  @Nonnull
  public static Optional<Set<String>> getAllFilesInResource(
    @Nonnull Class<?> clazz,
    @Nonnull String path,
    boolean recursive
  )
  throws URISyntaxException, IOException{

    // Assert parameters
    Validation.assertNonnull(clazz, "clazz");
    Validation.assertNonnull(path, "path");

    // Fix path if not ending with a slash
    //if(!path.endsWith("/")) path += "/";

    // Set up bin
    Set<String> files = new HashSet<>();

    // Start with first path
    Optional<Set<FileOrDirectory>> result = getResourceListing(clazz, path);

    // Exit if directory doesn't exist
    if(!result.isPresent()) return Optional.empty();

    // Filter files
    for(FileOrDirectory item : result.get()){

      // Get path if file
      if(item.isFile()) files.add(item.getName());

        // Only if recursive is enabled, then get inner
      else if(recursive){
        Set<String> inner = getAllFilesInResource(
          clazz,
          path + "/" + item.getName(),
          true
        ).orElse(Collections.emptySet());
        files.addAll(inner.stream().map(file -> item.getName() + "/" + file).collect(Collectors.toSet()));
      }

    }

    // Return it
    return Optional.of(files);
  }

  /**
   * Returns the listing of resource filepath in resources area
   * <B>NOTE:</B> This is more reliable than just calling <i>getResource(String)</i> because in JAR files,
   * <i>getResource(String)</i> returns directories as a single file and it will fail. In this method, we detect
   * whether if we're loading from resources in a standard filesystem or in a JAR file. If it is in standard
   * filesystem, then it scans the files normally with File, but if it's a JAR, then we need to load it using JarFile
   * and scan entries, then parse filepaths to get what we're looking for.
   *
   * @param clazz class
   * @param path  path of resource directory
   * @return optional object that may contain the resource listing, if path doesn't exist, then object will be empty
   * @throws URISyntaxException thrown if there's an issue with URL syntax
   * @throws IOException        thrown if there's an issue with reading the JarFile
   */
  @Nonnull
  public static Optional<Set<FileOrDirectory>> getResourceListing(@Nonnull Class<?> clazz, @Nonnull String path)
  throws URISyntaxException, IOException{

    // Assert parameters
    Validation.assertNonnull(clazz, "clazz");
    Validation.assertNonnull(path, "path");

    // Get resource URL
    URL directoryURL = clazz.getClassLoader().getResource(path);

    // If a file path, return it
    if(directoryURL != null && directoryURL.getProtocol().equals("file")){
      File[] files = new File(directoryURL.toURI()).listFiles();
      if(files == null) return Optional.empty();
      return Optional.of(new HashSet<>(Arrays.stream(files).map(item -> new FileOrDirectory(
        item.getName(),
        path,
        item.isFile()
      )).collect(Collectors.toList())));
    }

    // If URL is null, then assume it's a jar file - try to get it again under new path
    if(directoryURL == null){
      String newPath = clazz.getName().replace(".", "/") + ".class";
      directoryURL = clazz.getClassLoader().getResource(newPath);
    }

    // If null again, then we panic
    if(directoryURL == null) throw new RuntimeException(f("Directory is null. '{}'", path));

    // If a file path, return it
    if(directoryURL.getProtocol().equals("file")){
      File[] files = new File(directoryURL.toURI()).listFiles();
      if(files == null) return Optional.empty();
      return Optional.of(new HashSet<>(Arrays.stream(files).map(item -> new FileOrDirectory(
        item.getName(),
        path,
        item.isFile()
      )).collect(Collectors.toList())));
    }

    // If it reports a jar file, then proceed, otherwise we don't know what to do
    if(!directoryURL.getProtocol().equals("jar")){
      throw new UnsupportedOperationException(f(
        "Cannot list files for URL '{}' because protocol '{}' is unknown",
        directoryURL,
        directoryURL.getProtocol()
      ));
    }

    // Strip out only the file in jar file path
    String jarPath = directoryURL.getPath().substring(5, directoryURL.getPath().indexOf("!"));

    // Create JarFile
    try(JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.toString()))){

      // Get enumeration of entries in the file
      Enumeration<JarEntry> entries = jarFile.entries();

      // Set up collection to collect file paths
      Set<FileOrDirectory> files = new HashSet<>();

      // Iterate through enumeration
      while(entries.hasMoreElements()){

        // Get the name
        String name = entries.nextElement().getName();

        // Filter the entries, only find files that we're looking for
        if(name.startsWith(path)){

          // Clean up name
          String entry = name.substring(path.length());
          if(entry.startsWith("/")) entry = entry.substring(1); // Remove leftover slash

          // If it's a directory, then just strip out the children in the path
          int checkSubDirectory = entry.indexOf("/");
          if(checkSubDirectory > -1) entry = entry.substring(0, checkSubDirectory);

          // Add to collection
          if(!entry.isEmpty()) files.add(new FileOrDirectory(entry, path, true)); // TODO!!
        }
      }

      // Return it
      return Optional.of(files);
    }
  }

  /**
   * Reads the contents of resource file into String
   *
   * @param clazz class
   * @param path  path of file in resources
   * @return Optional object that may contain contents of resource file. If the file doesn't exist, then object is returned empty
   * @throws IOException thrown when there's some issues with reading the file
   */
  @Nonnull
  public static Optional<String> getResourceFileContent(@Nonnull Class<?> clazz, @Nonnull String path)
  throws IOException{

    // Set up builder
    StringBuilder builder = new StringBuilder();

    // Open stream
    try(InputStream is = getResourceStream(clazz, path)){

      // Return empty if resource doesn't exist
      if(is == null) return Optional.empty();

      // Otherwise continue with the scan
      try(BufferedReader br = new BufferedReader(new InputStreamReader(is))){
        String line;
        while((line = br.readLine()) != null) builder.append(line).append("\n");
      }
    }

    // Return it
    return Optional.of(builder.toString());
  }

  /**
   * Retrieves resource stream from a filepath in classloader's resources
   *
   * @param clazz class
   * @param path  path
   * @return InputStream or null if file doesn't exist
   */
  @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
  @Nullable
  private static InputStream getResourceStream(@Nonnull Class<?> clazz, @Nonnull String path){
    InputStream is = Validation.assertNonnull(clazz, "clazz")
                               .getClassLoader()
                               .getResourceAsStream(Validation.assertNonnull(path, "path"));
    return is != null ? is : Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
  }

  /** Lightweight version of File */
  public static class FileOrDirectory{

    /** Name of file */
    @Nonnull
    private final String name;

    @Nonnull
    private final String path;

    /** Indicates whether this file is a file */
    private final boolean isFile;

    /**
     * FileOrDirectory constructor
     *
     * @param name   name of file
     * @param path   path of the file/directory
     * @param isFile true to indicate it is a file, otherwise false for directory
     */
    public FileOrDirectory(@Nonnull String name, @Nonnull String path, boolean isFile){
      this.name = Validation.assertNonemptyString(name, "name");
      this.path = path;
      this.isFile = isFile;
    }

    /**
     * Returns true if this is a file, false if this is a directory
     *
     * @return true if file, false if directory
     */
    public boolean isFile(){
      return isFile;
    }

    /**
     * Returns the name
     *
     * @return name
     */
    @Nonnull
    public String getName(){
      return name;
    }

    @Override
    public String toString(){
      return simpleToString(this);
    }

    /**
     * Returns the path
     *
     * @return path
     */
    @Nonnull
    public String getPath(){
      return path;
    }
  }
}
