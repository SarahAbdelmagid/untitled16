package com.java.twitter4j.tweets;

import com.github.sarxos.webcam.Webcam;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageEditor extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JLabel capturedImageLabel;
    private BufferedImage image;
    private JButton saveButton;
    private JButton cameraButton;
    private JButton shareButton;
    private JPanel colorPanel;
    private JButton grayscaleButton;
    private JButton invertButton;
    private JTextField twitterAccountField;
    private Webcam webcam;

    public ImageEditor(JLabel imageLabel) {
        this.imageLabel = imageLabel;
        initUI();
    }

    private void initUI() {
        setTitle("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 500));

        imagePanel = new JPanel();
        imagePanel.setLayout(new GridBagLayout());
        imageLabel = new JLabel();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;

        image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 200, 200);
        g.dispose();

        GridBagConstraints imageConstraints = new GridBagConstraints();
        imageConstraints.anchor = GridBagConstraints.WEST;
        imageConstraints.gridx = 0;
        imageConstraints.gridy = 0;
        imageConstraints.gridwidth = 1;
        imagePanel.add(imageLabel, imageConstraints);

        JPanel outputPanel = new JPanel(new BorderLayout());
        capturedImageLabel = new JLabel();
        outputPanel.add(capturedImageLabel, BorderLayout.WEST);
        outputPanel.add(imagePanel, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        saveButton = new JButton("Save");
        saveButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        saveButton.addActionListener(new SaveButtonListener());
        cameraButton = new JButton("Camera");
        cameraButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        cameraButton.addActionListener(new CameraButtonListener());
        shareButton = new JButton("Share");
        shareButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        shareButton.addActionListener(new ShareButtonListener());
        saveButton.setBorderPainted(false); // إزالة الإطار الافتراضي
        shareButton.setBorderPainted(false); // إزالة الإطار الافتراضي
        cameraButton.setPreferredSize(new Dimension(420, 420));
        saveButton.setPreferredSize(new Dimension(720, 720));
        shareButton.setPreferredSize(new Dimension(720, 720));



        optionsPanel.add(saveButton);
        optionsPanel.add(cameraButton);
        optionsPanel.add(shareButton);

        colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Color Options"));
        colorPanel.setPreferredSize(new Dimension(105, 0));

        grayscaleButton = new JButton("Grayscale");
        grayscaleButton.addActionListener(new GrayscaleButtonListener());
        invertButton = new JButton("Invert");
        invertButton.addActionListener(new InvertButtonListener());
        colorPanel.add(grayscaleButton);
        colorPanel.add(invertButton);

        JPanel colorOptionsPanel = new JPanel();
        colorOptionsPanel.setLayout(new BorderLayout());
        colorOptionsPanel.add(colorPanel, BorderLayout.EAST);

        JPanel dummyPanel = new JPanel();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        buttonsPanel.add(saveButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        buttonsPanel.add(cameraButton);

        JPanel sharePanel = new JPanel();
        sharePanel.setLayout(new BoxLayout(sharePanel, BoxLayout.Y_AXIS));
        sharePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sharePanel.add(shareButton);

        optionsPanel.add(Box.createRigidArea(new Dimension(0, 0)));
        optionsPanel.add(buttonsPanel);
        optionsPanel.add(sharePanel);


        JPanel optionsButtonsPanel = new JPanel();
        optionsButtonsPanel.setLayout(new BorderLayout());
        optionsButtonsPanel.add(dummyPanel, BorderLayout.NORTH);
        optionsButtonsPanel.add(buttonsPanel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(outputPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(colorOptionsPanel)
                        .addComponent(optionsButtonsPanel)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(saveButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(shareButton))));

        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(outputPanel)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(colorOptionsPanel)
                        .addComponent(optionsButtonsPanel)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(saveButton)
                                .addComponent(shareButton))));

        getContentPane().add(mainPanel);

        pack();
        setLocationRelativeTo(null);
    }

    private void updateImage(BufferedImage newImage) {
        image = newImage;
        Image scaledImage = image.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        imageLabel.setVerticalAlignment(JLabel.TOP); // تحديد موقع الصورة في الجزء العلوي

        // تحديث القيود لنقل الصورة إلى حافة الشاشة العلوية
        GridBagConstraints imageConstraints = new GridBagConstraints();
        imageConstraints.anchor = GridBagConstraints.NORTHWEST;
        imageConstraints.gridx = 0;
        imageConstraints.gridy = 0;
        imageConstraints.gridwidth = 1;
        imageConstraints.weightx = 1.0;
        imageConstraints.weighty = 0.0;
        imageConstraints.fill = GridBagConstraints.NONE;
        imageConstraints.insets = new Insets(0, 0, 140, 0);
        imagePanel.add(imageLabel, imageConstraints);

        revalidate(); // إعادة تحميل المكونات لتحديث العرض
    }





    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while saving the image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void shareImage() {
        String twitterAccount = twitterAccountField.getText().trim();
        if (twitterAccount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Twitter account.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            File tempFile = File.createTempFile("temp_image", ".png");
            ImageIO.write(image, "png", tempFile);

            Twitter twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer("g8JFkdkVJqrK61xm4TXzOpmcN", "kDfbQdbLOI6KWz3UDIQkTn5rmmUazVPFz4JK61uhh4NUFZR0wO");
            twitter.setOAuthAccessToken(new AccessToken("1655286229338845185-dR1ZEmbzZi0kahDhqa9v7wY8eWbuhz", "pMbSqqhhkZCut18nER9uyjjt797KGbhi7nwpCyjjTKJeM"));


            ImageUpload imageUpload = new ImageUploadFactory().getInstance(MediaProvider.TWITTER);
            String imageUrl = imageUpload.upload(tempFile);

            StatusUpdate statusUpdate = new StatusUpdate("Check out this image!")
                    .media(tempFile);

            Status status = twitter.updateStatus(statusUpdate);

            tempFile.delete();

            JOptionPane.showMessageDialog(this, "Image shared successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (TwitterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while sharing the image.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void captureImage() {
        try {
            if (webcam == null) {
                webcam = Webcam.getDefault();
                webcam.open();
            }

            cameraButton.setEnabled(false); // تعطيل زر الكاميرا

            BufferedImage capturedImage = webcam.getImage();
            updateImage(capturedImage);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while capturing the image.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            cameraButton.setEnabled(true); // تمكين زر الكاميرا بعد الانتهاء من التقاط الصورة وتحديث العرض
        }
    }



    private void applyGrayscale() {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int average = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                int grayscaleRGB = new Color(average, average, average).getRGB();
                newImage.setRGB(x, y, grayscaleRGB);
            }
        }

        updateImage(newImage);
    }

    private void applyInvert() {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int invertedRGB = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()).getRGB();
                newImage.setRGB(x, y, invertedRGB);
            }
        }

        updateImage(newImage);
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveImage();
        }
    }

    private class ShareButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            shareImage();
        }
    }

    private class CameraButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            captureImage();
        }
    }

    private class GrayscaleButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            applyGrayscale();
        }
    }

    private class InvertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            applyInvert();
        }
    }
}

public class ImageEditorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JLabel imageLabel = new JLabel();
            ImageEditor imageEditor = new ImageEditor(imageLabel);
            imageEditor.setVisible(true);
        });
    }
}

