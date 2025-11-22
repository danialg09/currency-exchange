package ru.skillbox.currency.exchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyShortDto;
import ru.skillbox.currency.exchange.dto.ListCurrencyDto;
import ru.skillbox.currency.exchange.dto.xml.ValuteDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.util.MappingUtils;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MappingUtils.class})
public interface CurrencyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "charCode", target = "isoCharCode")
    @Mapping(source = "numCode", target = "isoNumCode")
    @Mapping(source = "value", target = "value")
    Currency toEntity(ValuteDto dto);

    CurrencyDto convertToDto(Currency currency);

    Currency convertToEntity(CurrencyDto currencyDto);

    List<CurrencyShortDto> convertToDtoList(List<Currency> currencyList);

    default ListCurrencyDto convertToListCurrencyDto(List<Currency> currencyList) {
        ListCurrencyDto listCurrencyDto = new ListCurrencyDto();
        listCurrencyDto.setCurrencies(convertToDtoList(currencyList));
        return listCurrencyDto;
    }
}
