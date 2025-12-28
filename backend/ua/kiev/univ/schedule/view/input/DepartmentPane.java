package ua.kiev.univ.schedule.view.input;

import ua.kiev.univ.schedule.model.department.Department;
import ua.kiev.univ.schedule.model.department.Faculty;
import ua.kiev.univ.schedule.service.department.DepartmentService;
import ua.kiev.univ.schedule.service.department.FacultyService;
import ua.kiev.univ.schedule.util.Language;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.description.DescriptionedPane;

public abstract class DepartmentPane<E extends Department> extends SplitPane {

    private final String key;
    private final InputPane inputPane;
    public final DescriptionedPane<Faculty> facultyPane;
    public final DescriptionedPane<E> departmentPane;

    public DepartmentPane(InputPane inputPane, DepartmentService<E> departmentService, String key) {
        super(HORIZONTAL_SPLIT);
        this.inputPane = inputPane;
        this.key = key;

        FacultyService<E> facultyService = new FacultyService<>();
        departmentService.setFacultyService(facultyService);

        facultyPane = new DescriptionedPane<>(facultyService, key + ".faculty");
        departmentPane = new DescriptionedPane<>(departmentService, key + ".department");

        facultyService.setDepartmentPane(departmentPane);

        this.setLeftComponent(facultyPane);
        this.setRightComponent(departmentPane);
    }

    @Override
    public void refresh() {
        facultyPane.refresh();
        departmentPane.refresh();
    }

    @Override
    public void loadLanguage() {
        inputPane.setTitleAt(inputPane.indexOfComponent(this), Language.getText(key));
        facultyPane.loadLanguage();
        departmentPane.loadLanguage();
    }
}