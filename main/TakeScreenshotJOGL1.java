import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

import javax.imageio.*;

import net.java.games.jogl.*;
import net.java.games.jogl.util.BufferUtils;

/**
 * Takes screenshots in JOGL; requires you to have JOGL installed and running
 * correctly - c.f. http://javagamesfactory.org/views/view-library?name=JOGL
 * 
 * @author kevglass
 */
public class TakeScreenshotJOGL1
{
  public static void writeBufferToFile( GLDrawable drawable, File outputFile )
  {
    int width = drawable.getSize().width;
    int height = drawable.getSize().height;

    ByteBuffer pixelsRGB = BufferUtils.newByteBuffer( width * height * 3 );

    GL gl = drawable.getGL();

    gl.glReadBuffer( GL.GL_BACK );
    gl.glPixelStorei( GL.GL_PACK_ALIGNMENT, 1 );

    gl.glReadPixels( 0, // GLint x
        0, // GLint y
        width, // GLsizei width
        height, // GLsizei height
        GL.GL_RGB, // GLenum format
        GL.GL_UNSIGNED_BYTE, // GLenum type
        pixelsRGB ); // GLvoid *pixels

    int[] pixelInts = new int[ width * height ];

    /* Convert RGB bytes to ARGB ints with no transparency. Flip image
    * vertically by reading the
    * rows of pixels in the byte buffer in reverse - (0,0) is at bottom
    * left
    * in OpenGL.
    */

    int p = width * height * 3; // Points to first byte (red) in each row.
    int q; // Index into ByteBuffer
    int i = 0; // Index into target int[]
    int w3 = width * 3; // Number of bytes in each row

    for( int row = 0; row < height; row++ )
    {
      p -= w3;
      q = p;
      for( int col = 0; col < width; col++ )
      {
        int iR = pixelsRGB.get( q++ );
        int iG = pixelsRGB.get( q++ );
        int iB = pixelsRGB.get( q++ );

        pixelInts[ i++ ] = 0xFF000000 | ( ( iR & 0x000000FF ) << 16 )
            | ( ( iG & 0x000000FF ) << 8 ) | ( iB & 0x000000FF );
      }

    }

    BufferedImage bufferedImage = new BufferedImage( width, height,
        BufferedImage.TYPE_INT_ARGB );

    bufferedImage.setRGB( 0, 0, width, height, pixelInts, 0, width );

    try
    {
      ImageIO.write( bufferedImage, "PNG", outputFile );
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }

  }
}