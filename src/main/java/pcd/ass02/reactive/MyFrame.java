package pcd.ass02.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;

public class MyFrame extends JFrame {

    private final Controller controller;
    private final JButton button;

    public MyFrame(final Controller controller) {
        super("GUI");
        this.controller = controller;
        this.button = new JButton("Press me");
    }

    public void startFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(".")); // start at application current directory
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File rootFolder = fc.getSelectedFile();
            this.controller.setRootPath(rootFolder.getPath());
        }
    }

    public void startGUI(final Observable<String> source) {
        setSize(150,60);
        this.button.addActionListener((final ActionEvent ev) -> {
            //stream.onNext(1);
            source.subscribeOn(Schedulers.io())
                    .subscribe(s -> {
                        System.out.println("source: " + s);
                        Thread.sleep(500);
                    });
        });
        getContentPane().add(this.button);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
        });
        setVisible(true);
    }

}
