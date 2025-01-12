package pb1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String file = "src/main/resources/angajati.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        List<Angajat> angajati = new ArrayList<>();

        try {
            angajati = mapper.readValue(new File(file), new TypeReference<List<Angajat>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.println("\nMeniu:");
            System.out.println("1. Afișarea listei de angajați");
            System.out.println("2. Afișarea angajaților care au salariul peste 2500 RON");
            System.out.println("3. Afișarea angajaților din luna aprilie a anului trecut cu funcție de conducere");
            System.out.println("4. Afișarea angajaților fără funcție de conducere, în ordine descrescătoare a salariilor");
            System.out.println("5. Extragerea și afișarea numelor angajaților scrise cu majuscule");
            System.out.println("6. Afișarea salariilor mai mici de 3000 RON");
            System.out.println("7. Afișarea datelor primului angajat al firmei");
            System.out.println("8. Afișarea de statistici referitoare la salariul angajaților");
            System.out.println("9. Verificare dacă există cel puțin un Ion printre angajați");
            System.out.println("10. Afișarea numărului de persoane angajate în vara anului precedent");
            System.out.println("11. Ieșirea din program");
            System.out.print("Alege o opțiune: ");

            int n = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (n) {
                case 1:
                    angajati.forEach(System.out::println);
                    break;
                case 2:
                    angajati.stream()
                            .filter(angajat -> angajat.getSalariul() > 2500)
                            .forEach(System.out::println);
                    break;
                case 3:
                    int an_curent = LocalDate.now().getYear();
                    List<Angajat> aprilManagers = angajati.stream()
                            .filter(angajat -> angajat.getData_angajarii().getYear() == an_curent - 1 &&
                                    angajat.getData_angajarii().getMonth() == Month.APRIL &&
                                    (angajat.getPostul().toLowerCase().contains("sef") ||
                                            angajat.getPostul().toLowerCase().contains("director")))
                            .collect(Collectors.toList());
                    aprilManagers.forEach(System.out::println);
                    break;
                case 4:
                    angajati.stream()
                            .filter(angajat -> !angajat.getPostul().toLowerCase().contains("director") &&
                                    !angajat.getPostul().toLowerCase().contains("sef"))
                            .sorted(Comparator.comparing(Angajat::getSalariul).reversed())
                            .forEach(System.out::println);
                    break;
                case 5:
                    List<String> nume_angajat = angajati.stream()
                            .map(angajat -> angajat.getNumele().toUpperCase())
                            .toList();
                    nume_angajat.forEach(System.out::println);
                    break;
                case 6:
                    angajati.stream()
                            .map(Angajat::getSalariul)
                            .filter(salariul -> salariul < 3000)
                            .forEach(System.out::println);
                    break;
                case 7:
                    Optional<Angajat> primul_angajat = angajati.stream()
                            .min(Comparator.comparing(Angajat::getData_angajarii));
                    primul_angajat.ifPresentOrElse(
                            System.out::println,
                            () -> System.out.println("Nu există angajați în firmă."));
                    break;
                case 8:
                    DoubleSummaryStatistics statistica = angajati.stream()
                            .collect(Collectors.summarizingDouble(Angajat::getSalariul));
                    System.out.println("Salariul mediu: " + statistica.getAverage());
                    System.out.println("Salariul minim: " + statistica.getMin());
                    System.out.println("Salariul maxim: " + statistica.getMax());
                    break;
                case 9:
                    angajati.stream()
                            .map(Angajat::getNumele)
                            .filter(name -> name.contains("Ion"))
                            .findAny()
                            .ifPresentOrElse(
                                    name -> System.out.println("Firma are cel puțin un Ion angajat"),
                                    () -> System.out.println("Firma nu are nici un Ion angajat")
                            );
                    break;
                case 10:
                    int an_precedent = LocalDate.now().getYear() - 1;
                    long angajati_vara = angajati.stream()
                            .filter(angajat -> angajat.getData_angajarii().getYear() == an_precedent && (angajat.getData_angajarii().getMonth() == Month.JUNE || angajat.getData_angajarii().getMonth() == Month.JULY || angajat.getData_angajarii().getMonth() == Month.AUGUST))
                            .count();
                    System.out.println("Numarul de persoane angajate in vara anului precedent: " + angajati_vara);
                    break;
            }
        }
    }
}