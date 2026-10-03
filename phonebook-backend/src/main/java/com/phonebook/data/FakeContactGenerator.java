package com.phonebook.data;

import com.phonebook.entity.Contact;
import com.phonebook.repository.ContactRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FakeContactGenerator implements CommandLineRunner {

    private static final int TOTAL_CONTACTS = 5000;
    private static final int BATCH_SIZE = 500;

    private final ContactRepository contactRepository;
    private final Faker faker = new Faker(new Locale("en-IN"));

    public FakeContactGenerator(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public void run(String... args) {

        long existingCount = contactRepository.count();

        if (existingCount > 0) {
            System.out.println(
                "Fake contact generation skipped. Existing contacts: "
                    + existingCount
            );
            return;
        }

        List<Contact> contacts =
            new ArrayList<>(BATCH_SIZE);

        Set<String> usedNames =
            new HashSet<>();

        Set<String> usedPhoneNumbers =
            new HashSet<>();

        Set<String> usedEmails =
            new HashSet<>();

        Map<String, String> cityStateMap =
            new LinkedHashMap<>();

        cityStateMap.put("Mumbai", "Maharashtra");
        cityStateMap.put("Pune", "Maharashtra");
        cityStateMap.put("Nashik", "Maharashtra");
        cityStateMap.put("Nagpur", "Maharashtra");
        cityStateMap.put("Jalgaon", "Maharashtra");
        cityStateMap.put("Aurangabad", "Maharashtra");
        cityStateMap.put("Ahmednagar", "Maharashtra");
        cityStateMap.put("Thane", "Maharashtra");
        cityStateMap.put("Navi Mumbai", "Maharashtra");
        cityStateMap.put("Kolhapur", "Maharashtra");
        cityStateMap.put("Solapur", "Maharashtra");
        cityStateMap.put("Amravati", "Maharashtra");
        cityStateMap.put("Dhule", "Maharashtra");
        cityStateMap.put("Nanded", "Maharashtra");
        cityStateMap.put("Satara", "Maharashtra");
        cityStateMap.put("Sangli", "Maharashtra");
        cityStateMap.put("Akola", "Maharashtra");
        cityStateMap.put("Latur", "Maharashtra");
        cityStateMap.put("Jalna", "Maharashtra");
        cityStateMap.put("Chandrapur", "Maharashtra");

        cityStateMap.put("Ahmedabad", "Gujarat");
        cityStateMap.put("Surat", "Gujarat");
        cityStateMap.put("Vadodara", "Gujarat");
        cityStateMap.put("Rajkot", "Gujarat");
        cityStateMap.put("Gandhinagar", "Gujarat");
        cityStateMap.put("Bhavnagar", "Gujarat");

        cityStateMap.put("Indore", "Madhya Pradesh");
        cityStateMap.put("Bhopal", "Madhya Pradesh");
        cityStateMap.put("Gwalior", "Madhya Pradesh");
        cityStateMap.put("Jabalpur", "Madhya Pradesh");
        cityStateMap.put("Ujjain", "Madhya Pradesh");

        cityStateMap.put("Bengaluru", "Karnataka");
        cityStateMap.put("Mysuru", "Karnataka");
        cityStateMap.put("Mangaluru", "Karnataka");
        cityStateMap.put("Hubballi", "Karnataka");
        cityStateMap.put("Belagavi", "Karnataka");

        cityStateMap.put("Panaji", "Goa");
        cityStateMap.put("Margao", "Goa");
        cityStateMap.put("Vasco da Gama", "Goa");

        cityStateMap.put("Jaipur", "Rajasthan");
        cityStateMap.put("Jodhpur", "Rajasthan");
        cityStateMap.put("Udaipur", "Rajasthan");
        cityStateMap.put("Kota", "Rajasthan");
        cityStateMap.put("Ajmer", "Rajasthan");

        List<String> cityList =
            new ArrayList<>(cityStateMap.keySet());

        Random random = new Random();

        int generatedCount = 0;

        System.out.println(
            "Starting generation of "
                + TOTAL_CONTACTS
                + " fake contacts..."
        );

        while (generatedCount < TOTAL_CONTACTS) {

            String name;

            do {
                name = faker.name().fullName();
            } while (!usedNames.add(name));

            String phoneNumber;

            do {
                int firstDigit =
                    6 + random.nextInt(4);

                StringBuilder phoneBuilder =
                    new StringBuilder();

                phoneBuilder.append(firstDigit);

                for (int i = 0; i < 9; i++) {
                    phoneBuilder.append(
                        random.nextInt(10)
                    );
                }

                phoneNumber =
                    phoneBuilder.toString();

            } while (!usedPhoneNumbers.add(phoneNumber));

            String emailName =
                name
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", ".")
                    .replaceAll("^\\.|\\.$", "");

            String email;

            do {
                email =
                    emailName
                    + "."
                    + (generatedCount + 1)
                    + "@example.com";

            } while (!usedEmails.add(email));

            String city =
                cityList.get(
                    random.nextInt(cityList.size())
                );

            String state =
                cityStateMap.get(city);

            int pincode =
                100000 + random.nextInt(899999);

            int houseNumber =
                1 + random.nextInt(999);

            int streetNumber =
                1 + random.nextInt(50);

            String address =
                houseNumber
                + ", "
                + faker.address().streetName()
                + ", Lane "
                + streetNumber
                + ", "
                + city
                + ", "
                + state
                + " - "
                + pincode;

            Contact contact =
                new Contact(
                    name,
                    phoneNumber,
                    email,
                    address
                );

            contacts.add(contact);

            generatedCount++;

            if (contacts.size() == BATCH_SIZE) {

                contactRepository.saveAll(contacts);

                System.out.println(
                    "Inserted "
                    + generatedCount
                    + " / "
                    + TOTAL_CONTACTS
                );

                contacts.clear();
            }
        }

        if (!contacts.isEmpty()) {
            contactRepository.saveAll(contacts);
        }

        long finalCount =
            contactRepository.count();

        System.out.println(
            "========================================"
        );

        System.out.println(
            "Fake contact generation completed."
        );

        System.out.println(
            "Contacts generated: "
                + generatedCount
        );

        System.out.println(
            "Contacts in database: "
                + finalCount
        );

        System.out.println(
            "========================================"
        );
    }
}

