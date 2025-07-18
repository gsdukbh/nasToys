package top.werls.nastoys.common.utils.captcha;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Iterator;


/**
 * 抽象验证码<br> 抽象验证码实现了验证码字符串的生成、验证，验证码图片的写出<br> 实现类通过实现{@link #createImage(String)} 方法生成图片对象
 *
 * @author looly
 */
public abstract class AbstractCaptcha implements ICaptcha {

  @Serial
  private static final long serialVersionUID = 3180820918087507254L;

  /**
   * 图片的宽度
   */
  protected int width;
  /**
   * 图片的高度
   */
  protected int height;
  /**
   * 验证码干扰元素个数
   */
  protected int interfereCount;
  /**
   * 字体
   */
  protected Font font;
  /**
   * 验证码
   */
  protected String code;
  /**
   * 验证码图片
   */
  protected byte[] imageBytes;
  /**
   * 验证码生成器
   */
  protected CodeGenerator generator;
  /**
   * 背景色
   */
  protected Color background;
  /**
   * 文字透明度
   */
  protected AlphaComposite textAlpha;

  /**
   * 构造，使用随机验证码生成器生成验证码
   *
   * @param width          图片宽
   * @param height         图片高
   * @param codeCount      字符个数
   * @param interfereCount 验证码干扰元素个数
   */
  public AbstractCaptcha(int width, int height, int codeCount, int interfereCount) {
    this(width, height, new RandomGeneratorCode(codeCount), interfereCount);
  }

  /**
   * 构造
   *
   * @param width          图片宽
   * @param height         图片高
   * @param generator      验证码生成器
   * @param interfereCount 验证码干扰元素个数
   */
  public AbstractCaptcha(int width, int height, CodeGenerator generator, int interfereCount) {
    this.width = width;
    this.height = height;
    this.generator = generator;
    this.interfereCount = interfereCount;
    // 字体高度设为验证码高度-2，留边距
    this.font = new Font(Font.SANS_SERIF, Font.PLAIN, (int) (this.height * 0.75));
  }

  @Override
  public void createCode() {
    generateCode();
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    writePng(createImage(this.code), out);
    this.imageBytes = out.toByteArray();
  }

  private void writePng(Image image, OutputStream out) {
    try {
      ImageOutputStream result = ImageIO.createImageOutputStream(out);
      BufferedImage bufferedImage = new BufferedImage(
          image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
      Graphics2D g = bufferedImage.createGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();
      g.setColor(this.background != null ? this.background : Color.WHITE);
      g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      ImageWriter writer = getWriter(bufferedImage);
      if (writer == null) {
        return;
      }
      writer.setOutput(result);
      writer.write(bufferedImage);
      result.flush();
      writer.dispose();
    } catch (IOException e) {
      e.printStackTrace();

    }
  }

  public static ImageWriter getWriter(BufferedImage img) {
    final ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(img);
    final Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, "png");
    return iter.hasNext() ? iter.next() : null;
  }

  /**
   * 生成验证码字符串
   */
  protected void generateCode() {
    this.code = generator.generate();
  }

  /**
   * 根据生成的code创建验证码图片
   *
   * @param code 验证码
   * @return Image
   */
  protected abstract Image createImage(String code);

  @Override
  public String getCode() {
    if (null == this.code) {
      createCode();
    }
    return this.code;
  }

  @Override
  public Boolean validation(String userInputCode) {
    return this.generator.verify(getCode(), userInputCode);
  }

  /**
   * 验证码写出到文件
   *
   * @param path 文件路径
   * @throws IOException IO异常
   */
  public void write(String path) throws IOException {
    this.write(new File(path));
  }

  /**
   * 验证码写出到文件
   *
   * @param file 文件
   * @throws IOException IO异常
   */
  public void write(File file) throws IOException {
    try (OutputStream out = new FileOutputStream(file)) {
      this.write(out);
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(OutputStream out) {
    try {
      out.write(getImageBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取图形验证码图片bytes
   *
   * @return 图形验证码图片bytes
   */
  public byte[] getImageBytes() {
    if (null == this.imageBytes) {
      createCode();
    }
    return this.imageBytes;
  }

  /**
   * 获取验证码图
   *
   * @return 验证码图
   */
  public BufferedImage getImage() {
    try {
      InputStream inputStream = new ByteArrayInputStream(getImageBytes());
      return ImageIO.read(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获得图片的Base64形式
   *
   * @return 图片的Base64
   */
  public String getImageBase64() {
    return Base64.getEncoder().encodeToString(getImageBytes());
  }


  /**
   * 自定义字体
   *
   * @param font 字体
   */
  public void setFont(Font font) {
    this.font = font;
  }

  /**
   * 获取验证码生成器
   *
   * @return 验证码生成器
   */
  public CodeGenerator getGenerator() {
    return generator;
  }

  /**
   * 设置验证码生成器
   *
   * @param generator 验证码生成器
   */
  public void setGenerator(CodeGenerator generator) {
    this.generator = generator;
  }

  /**
   * 设置背景色
   *
   * @param background 背景色
   * @since 4.1.22
   */
  public void setBackground(Color background) {
    this.background = background;
  }

  /**
   * 设置文字透明度
   *
   * @param textAlpha 文字透明度，取值0~1，1表示不透明
   */
  public void setTextAlpha(float textAlpha) {
    this.textAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha);
  }

}
