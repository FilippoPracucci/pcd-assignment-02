package pcd.ass02.reactive;

import javax.swing.*;

public class Test {

    public static void main(String[] args) {
        final DependencyAnalyser analyser = new DependencyAnalyser();

        SwingUtilities.invokeLater(()->{
            new MyFrame(analyser.getSource());
        });


    }
}
