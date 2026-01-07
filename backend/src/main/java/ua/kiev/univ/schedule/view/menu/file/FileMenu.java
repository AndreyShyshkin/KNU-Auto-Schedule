package ua.kiev.univ.schedule.view.menu.file;

import ua.kiev.univ.schedule.view.Frame;
import ua.kiev.univ.schedule.view.menu.Menu;

import javax.swing.JFileChooser;

public class FileMenu extends Menu {

    private final JFileChooser chooser = new JFileChooser("saves");

    public FileMenu(Frame frame, String key) {
        super(key);
        this.add(new NewItem(frame, chooser, key + ".new"));
        this.add(new LoadItem(frame, chooser, key + ".load"));
        this.add(new SaveItem(frame, chooser, key + ".save"));
        this.add(new SaveasItem(frame, chooser, key + ".saveas"));
        this.add(new SaveResultsItem(frame, key + ".saveResults"));
        this.add(new ExitItem(frame, key + ".exit"));
    }
}