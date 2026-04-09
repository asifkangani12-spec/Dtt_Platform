package com.dtt.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private Map<String, CountryInfo> iso2ToInfo;
    private Map<String, String> iso3ToIso2;

    @PostConstruct
    public void init() {
        Locale arabic = Locale.forLanguageTag("ar");

        this.iso2ToInfo = Arrays.stream(Locale.getISOCountries())
                .collect(Collectors.toUnmodifiableMap(
                        iso2 -> iso2,
                        iso2 -> {
                            Locale locale = Locale.of("", iso2);
                            return new CountryInfo(
                                    iso2,
                                    locale.getISO3Country(),
                                    locale.getDisplayCountry(Locale.ENGLISH),
                                    locale.getDisplayCountry(arabic)
                            );
                        }
                ));

        this.iso3ToIso2 = iso2ToInfo.values().stream()
                .collect(Collectors.toUnmodifiableMap(CountryInfo::iso3, CountryInfo::iso2));
    }

    public Optional<CountryInfo> getCountryInfo(String code) {
        if (code == null || code.isBlank()) return Optional.empty();

        String upper = code.strip().toUpperCase();

        return switch (upper.length()) {
            case 2 -> Optional.ofNullable(iso2ToInfo.get(upper));
            case 3 -> Optional.ofNullable(iso3ToIso2.get(upper)).map(iso2ToInfo::get);
            default -> Optional.empty();
        };
    }
}