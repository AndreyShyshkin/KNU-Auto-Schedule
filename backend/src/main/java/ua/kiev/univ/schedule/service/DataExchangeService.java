package ua.kiev.univ.schedule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.repository.*;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class DataExchangeService {

    private static final Logger log = LoggerFactory.getLogger(DataExchangeService.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Object> repositories;
    private final DataInitializationService dataInitializationService;

    public DataExchangeService(ObjectMapper objectMapper,
                               BuildingRepository buildingRepository,
                               LessonTypeRepository lessonTypeRepository,
                               TimeRepository timeRepository,
                               DayRepository dayRepository,
                               FacultyRepository facultyRepository,
                               ChairRepository chairRepository,
                               SpecialityRepository specialityRepository,
                               TeacherRepository teacherRepository,
                               GroupRepository groupRepository,
                               EarmarkRepository earmarkRepository,
                               AuditoriumRepository auditoriumRepository,
                               SubjectRepository subjectRepository,
                               LessonRepository lessonRepository,
                               ScheduleVersionRepository scheduleVersionRepository,
                               AppointmentRepository appointmentRepository,
                               DataInitializationService dataInitializationService) {
        this.objectMapper = objectMapper;
        this.dataInitializationService = dataInitializationService;
        
        this.repositories = new LinkedHashMap<>();
        // Пріоритетний порядок для імпорту (спочатку ті, на кого посилаються інші)
        repositories.put("buildings", buildingRepository);
        repositories.put("faculties", facultyRepository);
        repositories.put("lesson_types", lessonTypeRepository);
        repositories.put("earmarks", earmarkRepository);
        repositories.put("days", dayRepository);
        repositories.put("times", timeRepository);
        repositories.put("subjects", subjectRepository);
        repositories.put("chairs", chairRepository);
        repositories.put("specialities", specialityRepository);
        repositories.put("teachers", teacherRepository);
        repositories.put("groups", groupRepository);
        repositories.put("auditoriums", auditoriumRepository);
        repositories.put("lessons", lessonRepository);
        repositories.put("schedule_versions", scheduleVersionRepository);
        repositories.put("appointments", appointmentRepository);
    }

    public List<String> getAvailableTables() {
        return new ArrayList<>(repositories.keySet());
    }

    public void exportToZip(List<String> tables, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (String tableName : tables) {
                Object repo = repositories.get(tableName);
                if (repo instanceof org.springframework.data.jpa.repository.JpaRepository) {
                    List<?> data = ((org.springframework.data.jpa.repository.JpaRepository<?, ?>) repo).findAll();
                    ZipEntry entry = new ZipEntry(tableName + ".json");
                    zos.putNextEntry(entry);
                    objectMapper.writeValue(zos, data);
                    zos.closeEntry();
                    log.info("Exported table: {}", tableName);
                }
            }
        }
    }

    @Transactional
    public Map<String, String> importFromZip(InputStream inputStream, List<String> tablesToImport) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, byte[]> entryDataMap = new HashMap<>();

        // 1. Спочатку зчитуємо всі обрані файли з ZIP в пам'ять
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                String tableName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;

                if (tablesToImport.contains(tableName)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    entryDataMap.put(tableName, baos.toByteArray());
                }
                zis.closeEntry();
            }
        }

        // 2. Імпортуємо дані у строгому порядку черговості (згідно з repositories map)
        for (String tableName : repositories.keySet()) {
            if (entryDataMap.containsKey(tableName)) {
                byte[] jsonBytes = entryDataMap.get(tableName);
                Object repo = repositories.get(tableName);
                
                if (repo instanceof org.springframework.data.jpa.repository.JpaRepository) {
                    try {
                        Class<?> entityClass = getEntityClass(tableName);
                        if (jsonBytes.length > 0) {
                            List<?> data = objectMapper.readValue(jsonBytes, objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass));
                            org.springframework.data.jpa.repository.JpaRepository jpaRepo = (org.springframework.data.jpa.repository.JpaRepository) repo;
                            
                            // Зберігаємо (update or insert)
                            jpaRepo.saveAll(data);
                            result.put(tableName, "Successfully imported " + data.size() + " items");
                            log.info("Imported table {}: {} items", tableName, data.size());
                        } else {
                            result.put(tableName, "Empty file");
                        }
                    } catch (Exception e) {
                        log.error("Error importing table " + tableName, e);
                        result.put(tableName, "Error: " + e.getMessage());
                    }
                }
            }
        }

        try {
            // Оновлюємо внутрішні списки DataService
            dataInitializationService.initializeData();
        } catch (Exception e) {
            log.error("Error refreshing DataService after import", e);
            result.put("_refresh_error", "Data saved, but cache refresh failed: " + e.getMessage());
        }
        
        return result;
    }

    private Class<?> getEntityClass(String tableName) {
        return switch (tableName) {
            case "buildings" -> ua.kiev.univ.schedule.model.placement.Building.class;
            case "lesson_types" -> ua.kiev.univ.schedule.model.lesson.LessonType.class;
            case "times" -> ua.kiev.univ.schedule.model.date.Time.class;
            case "days" -> ua.kiev.univ.schedule.model.date.Day.class;
            case "faculties" -> ua.kiev.univ.schedule.model.department.Faculty.class;
            case "chairs" -> ua.kiev.univ.schedule.model.department.Chair.class;
            case "specialities" -> ua.kiev.univ.schedule.model.department.Speciality.class;
            case "teachers" -> ua.kiev.univ.schedule.model.member.Teacher.class;
            case "groups" -> ua.kiev.univ.schedule.model.member.Group.class;
            case "earmarks" -> ua.kiev.univ.schedule.model.placement.Earmark.class;
            case "auditoriums" -> ua.kiev.univ.schedule.model.placement.Auditorium.class;
            case "subjects" -> ua.kiev.univ.schedule.model.subject.Subject.class;
            case "lessons" -> ua.kiev.univ.schedule.model.lesson.Lesson.class;
            case "schedule_versions" -> ua.kiev.univ.schedule.model.appointment.ScheduleVersion.class;
            case "appointments" -> ua.kiev.univ.schedule.model.appointment.Appointment.class;
            default -> throw new IllegalArgumentException("Unknown table: " + tableName);
        };
    }
}
