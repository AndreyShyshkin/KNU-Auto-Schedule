package ua.kiev.univ.schedule.view.input.description;

import ua.kiev.univ.schedule.model.department.DescriptionedEntity;
import ua.kiev.univ.schedule.service.department.DescriptionedEntityService;
import ua.kiev.univ.schedule.view.core.SplitPane;
import ua.kiev.univ.schedule.view.input.table.TablePane;

public class DescriptionedPane<E extends DescriptionedEntity> extends SplitPane {

    // Поле entityClass було в оригіналі, залишаємо для сумісності логіки, хоча воно може не використовуватись прямо тут
    private final Class<E> entityClass;
    public final TablePane<E> tablePane;
    public final DescriptionPane<E> descriptionPane;

    public DescriptionedPane(DescriptionedEntityService<E> service, String key) {
        super(VERTICAL_SPLIT);
        tablePane = new TablePane<>(service, key);
        descriptionPane = new DescriptionPane<>(service, key + ".description");

        service.setDescriptionPane(descriptionPane);

        this.setTopComponent(tablePane);
        this.setBottomComponent(descriptionPane);
        this.entityClass = service.entities.entityClass;
    }

    @Override
    public void refresh() {
        tablePane.refresh();
        descriptionPane.refresh();
    }

    @Override
    public void loadLanguage() {
        tablePane.loadLanguage();
        descriptionPane.loadLanguage();
    }
}