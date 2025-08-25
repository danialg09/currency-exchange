package ru.skillbox.currency.exchange.mapper;

import org.mapstruct.Mapper;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyShortDto;
import ru.skillbox.currency.exchange.dto.ListCurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    CurrencyDto convertToDto(Currency currency);

    Currency convertToEntity(CurrencyDto currencyDto);

    List<CurrencyShortDto> convertToDtoList(List<Currency> currencyList);

    default ListCurrencyDto convertToListCurrencyDto(List<Currency> currencyList) {
        ListCurrencyDto listCurrencyDto = new ListCurrencyDto();
        listCurrencyDto.setCurrencies(convertToDtoList(currencyList));
        return listCurrencyDto;
    }
}
