package top.werls.nastoys.common.file;

import top.werls.nastoys.common.file.impl.FileLocal;

import java.io.FileNotFoundException;

/**
 * date created 2022/7/27
 *
 * @author Jiawei Lee
 * @version TODO
 * @since on
 */
class FileManagersTest {

//  @Test
  void get() {
    FileManagers fileManagers =
        new FileLocal("D:\\Development\\Code\\Github\\spring-boot-template\\log");

   var file =  fileManagers.get("spring boot template-2022-07-20-0.log");

    System.out.println(file.length());
    System.out.println(file.getPath());
  }
//  @Test
  void  getByPath(){
      try {
          FileManagers fileManagers =
                  new FileLocal("D:\\Development\\Code\\Github\\spring-boot-template");
          var file= fileManagers.getByPath("/log/info/spring boot template-2022-07-20-0.log");
      System.out.println(file.getName());
      System.out.println(file.getPath());
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

  }
}
