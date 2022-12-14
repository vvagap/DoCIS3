package ru.sfu;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.sfu.entity.Television;

import java.util.List;
import java.util.Optional;

/**
 * Main class of the program
 * @author Agapchenko V.V.
 */
public class SpringApp {

    /**
     * Menu options enumeration
     */
    public enum Menu {
        INSERT,
        SHOW,
        DELETE,
        UPDATE,
        FIND,
        MENU,
        EXIT
    }

    /**
     * Show menu options
     */
    public static void showMenu() {
        System.out.println("Menu:");
        for (Menu option: Menu.values()) {
            System.out.printf(
                    "%s: %d%n",
                    option.toString(),
                    option.ordinal()
            );
        }
    }

    /**
     * Enter point to the program
     * @param args Optional arguments
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(
                        SpringConfig.class
                );

        TelevisionRepository repository =
                context.getBean(
                        "repositoryContainer",
                        RepositoryContainer.class
                ).getRepository();

        showMenu();

        List<Television> tvs;

        while (true) {
            int input = InputUtil.getInt(
                    "Menu > ",
                    Menu.values()[0].ordinal(),
                    Menu.values()[Menu.values().length-1].ordinal()
            );
            Menu choice = Menu.values()[input];
            switch (choice) {
                case INSERT -> {
                    try {
                        repository.save(new Television(
                                null,
                                InputUtil.getString("Model > "),
                                InputUtil.getString("Producer > "),
                                InputUtil.getString("Production Country > "),
                                InputUtil.getInt("Width (? > 0) > "),
                                InputUtil.getInt("Height (? > 0) > ")
                        ));
                    } catch (DataIntegrityViolationException ignored) {}
                }

                case SHOW -> {
                    tvs = repository.findAll();
                    if (tvs.size() > 0) {
                        tvs.forEach(System.out::println);
                    } else {
                        System.out.println(
                                "There are no televisions!"
                        );
                    }
                }

                case DELETE -> {
                    try {
                        repository.deleteById(
                                InputUtil.getInt("ID > ")
                        );
                    } catch (EmptyResultDataAccessException e) {
                        System.out.println(
                                "No television with such id exists!"
                        );
                    }
                }

                case FIND -> {
                    tvs = repository.findByWidthAndHeight(
                            InputUtil.getInt("Width > "),
                            InputUtil.getInt("Height > ")
                    );
                    if (tvs.size() > 0) {
                        tvs.forEach(System.out::println);
                    } else {
                        System.out.println(
                                "No television with such parameters exists!"
                        );
                    }
                }

                case UPDATE -> {
                    Optional<Television> optional =
                            repository.findById(InputUtil.getInt("ID > "));
                    if (optional.isEmpty()) {
                        System.out.println("Television was not found");
                        break;
                    }
                    Television tv = optional.get();
                    tv.setModel(InputUtil.getString("Model > "));
                    tv.setProducer(InputUtil.getString("Producer > "));
                    tv.setProductionCountry(InputUtil.getString("Production Country > "));
                    tv.setWidth(InputUtil.getInt("Width (? > 0) > "));
                    tv.setHeight(InputUtil.getInt("Height (? > 0) > "));
                    repository.save(tv);
                }

                case MENU -> showMenu();

                case EXIT -> {
                    context.close();
                    System.exit(0);
                }

                default -> System.out.println("No such option");
            }
        }
    }
}
