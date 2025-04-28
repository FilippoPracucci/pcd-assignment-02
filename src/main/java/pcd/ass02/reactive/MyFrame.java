package pcd.ass02.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;

public class MyFrame extends JFrame {

    private final JButton button;

    public MyFrame(final Observable<String> source){
        super("GUI");
        setSize(150,60);
        setVisible(true);
        this.button = new JButton("Press me");
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
    }

}
