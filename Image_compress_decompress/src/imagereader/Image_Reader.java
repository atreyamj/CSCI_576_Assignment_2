package imagereader;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class Image_Reader {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	BufferedImage img2;
	double Cosines[][] = new double[8][8];

	public void showIms(String[] args) {
		int width = Integer.parseInt(args[1]);
		int height = Integer.parseInt(args[2]);

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		try {
			File file = new File(args[0]);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			byte[] bytes = new byte[(int) len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			int ind = 0;
			for (int y = 0; y < height; y++) {

				for (int x = 0; x < width; x++) {

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind + height * width];
					byte b = bytes[ind + height * width * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x, y, pix);
					ind++;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after modification (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(img2));
		// img2=null;

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		//frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setCosines() {

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Cosines[i][j] = Math.cos((2 * i + 1) * j * 3.14159 / 16.00);
			}
		}

	}

	public BufferedImage encodeDCT() {
		int[][][] iaDCTImage = new int[512][512][3];
		int iH = img.getHeight();
		int iW = img.getWidth();
		int m=0,n=0;
		int[][] ImagePix=new int[iH][iW];
		for (int i = 0; i < iW; i += 8) {
			for (int j = 0; j < iH; j += 8) {
				for (int u = 0; u < 8; u++) {
					for (int v = 0; v < 8; v++) {
						float fCu = 1.0f, fCv = 1.0f;
						float fRRes = 0.00f, fGRes = 0.00f, fBRes = 0.00f;

						if (u == 0)
							fCu = 0.707f;
						if (v == 0)
							fCv = 0.707f;
						for (int x = 0; x < 8; x++) {
							for (int y = 0; y < 8; y++) {
								int iR, iG, iB;

								iR = (img.getRGB(i + x, j + y) >> 16) & 0xFF;
								iG = (img.getRGB(i + x, j + y) >> 8) & 0xFF;
								iB = img.getRGB(i + x, j + y) & 0xFF;

								fRRes += iR * Cosines[x][u] * Cosines[y][v];
								fGRes += iG * Cosines[x][u] * Cosines[y][v];
								fBRes += iB * Cosines[x][u] * Cosines[y][v];
								
							}
						}
						
						/*iaDCTImage[i + u][j + v][0] = (int) Math.round(fRRes * 0.25 * fCu * fCv / Math.pow(2, Quant));
						iaDCTImage[i + u][j + v][1] = (int) Math.round(fGRes * 0.25 * fCu * fCv / Math.pow(2, Quant));
						iaDCTImage[i + u][j + v][2] = (int) Math.round(fBRes * 0.25 * fCu * fCv / Math.pow(2, Quant));*/
						iaDCTImage[i + u][j + v][0] = (int) Math.round(fRRes );
						iaDCTImage[i + u][j + v][1] = (int) Math.round(fGRes );
						iaDCTImage[i + u][j + v][2] = (int) Math.round(fBRes );
					}
				}
			}
		}
		for (int i = 0; i < iW; i++) {
			for (int j = 0; j < iH; j++) {
				 int iColor = 0xff000000 | ((iaDCTImage[i][j][0] & 0xff) << 16) | ((iaDCTImage[i][j][1] & 0xff) << 8) | (iaDCTImage[i][j][2] & 0xff);
	                
				img2.setRGB(i, j, iColor);
			}
		}
		return img2;

	}

	public static void main(String[] args) {
		Image_Reader ren = new Image_Reader();
		//
		ren.setCosines();
		ren.showIms(args);
		ren.frame.setVisible(false);
		ren.lbIm2 = new JLabel(new ImageIcon(	ren.encodeDCT()));
		
		 ren.frame.revalidate();
		 ren.frame.repaint();
		 ren.frame.setVisible(true);
		// ren.frame.add(new JLabel(new ImageIcon(ren.encodeDCT(ren.img, 0))));
		// ren.encodeDCT(ren.img, 0);
		// ren.lbIm2 = new JLabel(new ImageIcon(ren.img2));
	}

}
