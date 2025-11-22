package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.skillbox.currency.exchange.dto.xml.ValCursDto;
import ru.skillbox.currency.exchange.dto.xml.ValuteDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CBRService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Value("${app.bank-url}")
    private String bankUrl;

    @PostConstruct
    public void init() {
        updateCurrencies();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateCurrencies() {
        log.info("CurrencyService method updateCurrencies executed");
        try {
            String xmlData = getXmlData();

            // 2. JAXB-ПАРСИНГ: Превращаем XML в Java-объект ValCursDto
            JAXBContext jaxbContext = JAXBContext.newInstance(ValCursDto.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ValCursDto response = (ValCursDto) unmarshaller.unmarshal(new StringReader(xmlData));

            // 👈 Вызываем новый метод
            response.getValutes().forEach(this::saveOrUpdateCurrency);

        } catch (Exception e) {
            // Если что-то пошло не так (ошибка URL, ошибка парсинга, ошибка БД)
            log.error("Failed to update currencies from URL: {}", bankUrl, e);
        }
    }

    private String getXmlData() throws IOException {
        URL url = new URL(bankUrl);
        // Читаем весь XML-ответ в строку
        StringBuilder xmlDataBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        url.openStream(), Charset.forName("Windows-1251")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xmlDataBuilder.append(line);
            }
        }
        return xmlDataBuilder.toString();
    }

    private void saveOrUpdateCurrency(ValuteDto valuteDto) {
        Optional<Currency> existingCurrency = currencyRepository.findByIsoCharCode(valuteDto.getCharCode());

        Currency newCurrency = currencyMapper.toEntity(valuteDto);

        if (existingCurrency.isPresent()) {
            Currency currencyToUpdate = existingCurrency.get();
            newCurrency.setId(currencyToUpdate.getId());

            log.debug("Updating existing currency: {}", newCurrency.getIsoCharCode());
        } else {
            log.debug("Creating new currency: {}", newCurrency.getIsoCharCode());
        }
        currencyRepository.save(newCurrency);
    }
}
