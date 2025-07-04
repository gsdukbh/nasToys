package top.werls.nastoys.common.utils.dynamicCompilation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Li JiaWei
 * @version 1
 * @date 2023/4/9
 * @since on
 */
@SpringBootTest
class DynamicCompileUtilsTest {


  @Test
  void compile() throws Exception {
    String code = """
        package dynamicCompilation;
            public class HelloWorld {
                 public String  hello(String msg){
                   return "Hello World! "+msg;
                  }
            }
        """;
    DynamicCompileUtils dynamicCompileUtils = new DynamicCompileUtils();
    var o = dynamicCompileUtils.compile(code,
        "dynamicCompilation.HelloWorld");
    var res = o.getClass().getMethod("hello", String.class).invoke(o, "compile");
    System.out.println(res);
  }
}