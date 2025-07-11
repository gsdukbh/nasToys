package top.werls.nastoys.common.file.impl;

import top.werls.nastoys.common.file.FileManagers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地目录实现
 *
 * @author Jiawei Lee
 * @version TODO
 * @date created 2022/7/20
 * @since on
 */
public class FileLocal implements FileManagers {
  private final String filePath;

  public FileLocal(String filePath) {
    this.filePath = filePath;
  }

  /**
   * 获取文件
   *
   * @param filename 文件名
   * @return file 对象 {@link File} 没有文件时返回null
   */
  @Override
  public File get(String filename) {
    File file = new File(filePath);
    List<File> list = new ArrayList<>();
    findPathFile(file, list);
    return list.stream()
        .filter(file1 -> file1.getName().equalsIgnoreCase(filename))
        .findFirst()
        .orElse(null);
  }

  @Override
  public File getByPath(String path) throws FileNotFoundException {
    if (path.startsWith("/") || path.startsWith("\\")) {
      return new File(filePath + path);
    } else {
      throw new FileNotFoundException("file must start with / or \\");
    }
  }

  private void findPathFile(File filename, List<File> list) {
    if (filename.isDirectory()) {
      var dirFiles = filename.listFiles();
      if (dirFiles != null) {
        for (var i : dirFiles) {
          findPathFile(i, list);
        }
      }
    } else {
      list.add(filename);
    }
  }

  /**
   * 从指定目录(桶)获取文件
   *
   * @param filename 文件名
   * @param path 目录 桶
   * @return file {@link File} 没有文件时返回null
   */
  @Override
  public File get(String filename, String path) throws FileNotFoundException {
    if (!path.startsWith("/") || path.endsWith("\\")) {
      throw new FileNotFoundException("file must start with / or \\");
    }
    File file = new File(filePath + path);
    List<File> list = new ArrayList<>();
    findPathFile(file, list);
    return list.stream()
        .filter(file1 -> file1.getName().equalsIgnoreCase(filename))
        .findFirst()
        .orElse(null);
  }

  /**
   * 从指定目录(桶)获取 输入流 方便minio
   *
   * @param filename 文件名
   * @param path 目录/ 桶
   * @return InputStream {@link InputStream}
   */
  @Override
  public InputStream getInputStream(String filename, String path) {
    return null;
  }

  /**
   * 从指定目录(桶)获取 输入流 方便minio
   *
   * @param filename 文件名
   * @return InputStream {@link InputStream}
   */
  @Override
  public InputStream getInputStream(String filename) {
    return null;
  }

  /**
   * 保存文件
   *
   * @param file file {@link File}
   */
  @Override
  public void save(InputStream file) {}

  /**
   * 保存文件
   *
   * @param file file file {@link File}
   * @param path 指定目录,or 桶
   */
  @Override
  public void save(InputStream file, String path) {}

  /**
   * 删除文件
   *
   * @param filename 文件名
   */
  @Override
  public void delete(String filename) {}

  /**
   * 删除文件
   *
   * @param filename 文件名
   * @param path 指定目录 or 桶
   */
  @Override
  public void delete(String filename, String path) {}
}
