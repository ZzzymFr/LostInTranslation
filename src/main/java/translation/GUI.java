package translation;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Translator translator = new JSONTranslator();
            CountryCodeConverter countryCodeConverter = new CountryCodeConverter();
            LanguageCodeConverter languageCodeConverter = new LanguageCodeConverter();

            // build sorted list of country names, keeping track of the code behind each name
            List<String> countryNames = translator.getCountryCodes().stream()
                    .map(countryCodeConverter::fromCountryCode)
                    .sorted()
                    .toList();

            // build sorted list of language names, keeping track of the code behind each name
            List<String> languageNames = translator.getLanguageCodes().stream()
                    .map(languageCodeConverter::fromLanguageCode)
                    .sorted(Comparator.naturalOrder())
                    .toList();

            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"));
            JComboBox<String> languageComboBox = new JComboBox<>(languageNames.toArray(new String[0]));
            languagePanel.add(languageComboBox);

            JLabel resultLabelText = new JLabel("Translation:");
            JLabel resultLabel = new JLabel("");
            JPanel resultPanel = new JPanel();
            resultPanel.add(resultLabelText);
            resultPanel.add(resultLabel);

            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane countryScrollPane = new JScrollPane(countryList);
            countryScrollPane.setPreferredSize(new java.awt.Dimension(300, 300));

            Runnable updateTranslation = () -> {
                String selectedCountry = countryList.getSelectedValue();
                String selectedLanguage = (String) languageComboBox.getSelectedItem();

                if (selectedCountry == null || selectedLanguage == null) {
                    return;
                }

                String countryCode = countryCodeConverter.fromCountry(selectedCountry);
                String languageCode = languageCodeConverter.fromLanguage(selectedLanguage);

                String result = translator.translate(countryCode, languageCode);
                if (result == null) {
                    result = "no translation found!";
                }
                resultLabel.setText(result);
            };

            countryList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateTranslation.run();
                }
            });
            languageComboBox.addActionListener(e -> updateTranslation.run());

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(countryScrollPane);
            mainPanel.add(resultPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            // select an initial country so the UI isn't blank on launch
            if (!countryNames.isEmpty()) {
                countryList.setSelectedIndex(0);
            }
        });
    }
}
