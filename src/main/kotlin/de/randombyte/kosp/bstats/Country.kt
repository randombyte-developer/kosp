package de.randombyte.kosp.bstats

import java.util.*

enum class Country(val isoTag: String, val countryName: String) {
    AUTO_DETECT("AUTO", "Auto Detected"),

    ANDORRA("AD", "Andorra"),
    UNITED_ARAB_EMIRATES("AE", "United Arab Emirates"),
    AFGHANISTAN("AF", "Afghanistan"),
    ANTIGUA_AND_BARBUDA("AG", "Antigua and Barbuda"),
    ANGUILLA("AI", "Anguilla"),
    ALBANIA("AL", "Albania"),
    ARMENIA("AM", "Armenia"),
    NETHERLANDS_ANTILLES("AN", "Netherlands Antilles"),
    ANGOLA("AO", "Angola"),
    ANTARCTICA("AQ", "Antarctica"),
    ARGENTINA("AR", "Argentina"),
    AMERICAN_SAMOA("AS", "American Samoa"),
    AUSTRIA("AT", "Austria"),
    AUSTRALIA("AU", "Australia"),
    ARUBA("AW", "Aruba"),
    ÅLAND_ISLANDS("AX", "Åland Islands"),
    AZERBAIJAN("AZ", "Azerbaijan"),
    BOSNIA_AND_HERZEGOVINA("BA", "Bosnia and Herzegovina"),
    BARBADOS("BB", "Barbados"),
    BANGLADESH("BD", "Bangladesh"),
    BELGIUM("BE", "Belgium"),
    BURKINA_FASO("BF", "Burkina Faso"),
    BULGARIA("BG", "Bulgaria"),
    BAHRAIN("BH", "Bahrain"),
    BURUNDI("BI", "Burundi"),
    BENIN("BJ", "Benin"),
    SAINT_BARTHÉLEMY("BL", "Saint Barthélemy"),
    BERMUDA("BM", "Bermuda"),
    BRUNEI("BN", "Brunei"),
    BOLIVIA("BO", "Bolivia"),
    BONAIRE_SINT_EUSTATIUS_AND_SABA("BQ", "Bonaire, Sint Eustatius and Saba"),
    BRAZIL("BR", "Brazil"),
    BAHAMAS("BS", "Bahamas"),
    BHUTAN("BT", "Bhutan"),
    BOUVET_ISLAND("BV", "Bouvet Island"),
    BOTSWANA("BW", "Botswana"),
    BELARUS("BY", "Belarus"),
    BELIZE("BZ", "Belize"),
    CANADA("CA", "Canada"),
    COCOS_ISLANDS("CC", "Cocos Islands"),
    THE_DEMOCRATIC_REPUBLIC_OF_CONGO("CD", "The Democratic Republic Of Congo"),
    CENTRAL_AFRICAN_REPUBLIC("CF", "Central African Republic"),
    CONGO("CG", "Congo"),
    SWITZERLAND("CH", "Switzerland"),
    CÔTE_D_IVOIRE("CI", "Côte d'Ivoire"),
    COOK_ISLANDS("CK", "Cook Islands"),
    CHILE("CL", "Chile"),
    CAMEROON("CM", "Cameroon"),
    CHINA("CN", "China"),
    COLOMBIA("CO", "Colombia"),
    COSTA_RICA("CR", "Costa Rica"),
    CUBA("CU", "Cuba"),
    CAPE_VERDE("CV", "Cape Verde"),
    CURAÇAO("CW", "Curaçao"),
    CHRISTMAS_ISLAND("CX", "Christmas Island"),
    CYPRUS("CY", "Cyprus"),
    CZECH_REPUBLIC("CZ", "Czech Republic"),
    GERMANY("DE", "Germany"),
    DJIBOUTI("DJ", "Djibouti"),
    DENMARK("DK", "Denmark"),
    DOMINICA("DM", "Dominica"),
    DOMINICAN_REPUBLIC("DO", "Dominican Republic"),
    ALGERIA("DZ", "Algeria"),
    ECUADOR("EC", "Ecuador"),
    ESTONIA("EE", "Estonia"),
    EGYPT("EG", "Egypt"),
    WESTERN_SAHARA("EH", "Western Sahara"),
    ERITREA("ER", "Eritrea"),
    SPAIN("ES", "Spain"),
    ETHIOPIA("ET", "Ethiopia"),
    FINLAND("FI", "Finland"),
    FIJI("FJ", "Fiji"),
    FALKLAND_ISLANDS("FK", "Falkland Islands"),
    MICRONESIA("FM", "Micronesia"),
    FAROE_ISLANDS("FO", "Faroe Islands"),
    FRANCE("FR", "France"),
    GABON("GA", "Gabon"),
    UNITED_KINGDOM("GB", "United Kingdom"),
    GRENADA("GD", "Grenada"),
    GEORGIA("GE", "Georgia"),
    FRENCH_GUIANA("GF", "French Guiana"),
    GUERNSEY("GG", "Guernsey"),
    GHANA("GH", "Ghana"),
    GIBRALTAR("GI", "Gibraltar"),
    GREENLAND("GL", "Greenland"),
    GAMBIA("GM", "Gambia"),
    GUINEA("GN", "Guinea"),
    GUADELOUPE("GP", "Guadeloupe"),
    EQUATORIAL_GUINEA("GQ", "Equatorial Guinea"),
    GREECE("GR", "Greece"),
    SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("GS", "South Georgia And The South Sandwich Islands"),
    GUATEMALA("GT", "Guatemala"),
    GUAM("GU", "Guam"),
    GUINEA_BISSAU("GW", "Guinea-Bissau"),
    GUYANA("GY", "Guyana"),
    HONG_KONG("HK", "Hong Kong"),
    HEARD_ISLAND_AND_MCDONALD_ISLANDS("HM", "Heard Island And McDonald Islands"),
    HONDURAS("HN", "Honduras"),
    CROATIA("HR", "Croatia"),
    HAITI("HT", "Haiti"),
    HUNGARY("HU", "Hungary"),
    INDONESIA("ID", "Indonesia"),
    IRELAND("IE", "Ireland"),
    ISRAEL("IL", "Israel"),
    ISLE_OF_MAN("IM", "Isle Of Man"),
    INDIA("IN", "India"),
    BRITISH_INDIAN_OCEAN_TERRITORY("IO", "British Indian Ocean Territory"),
    IRAQ("IQ", "Iraq"),
    IRAN("IR", "Iran"),
    ICELAND("IS", "Iceland"),
    ITALY("IT", "Italy"),
    JERSEY("JE", "Jersey"),
    JAMAICA("JM", "Jamaica"),
    JORDAN("JO", "Jordan"),
    JAPAN("JP", "Japan"),
    KENYA("KE", "Kenya"),
    KYRGYZSTAN("KG", "Kyrgyzstan"),
    CAMBODIA("KH", "Cambodia"),
    KIRIBATI("KI", "Kiribati"),
    COMOROS("KM", "Comoros"),
    SAINT_KITTS_AND_NEVIS("KN", "Saint Kitts And Nevis"),
    NORTH_KOREA("KP", "North Korea"),
    SOUTH_KOREA("KR", "South Korea"),
    KUWAIT("KW", "Kuwait"),
    CAYMAN_ISLANDS("KY", "Cayman Islands"),
    KAZAKHSTAN("KZ", "Kazakhstan"),
    LAOS("LA", "Laos"),
    LEBANON("LB", "Lebanon"),
    SAINT_LUCIA("LC", "Saint Lucia"),
    LIECHTENSTEIN("LI", "Liechtenstein"),
    SRI_LANKA("LK", "Sri Lanka"),
    LIBERIA("LR", "Liberia"),
    LESOTHO("LS", "Lesotho"),
    LITHUANIA("LT", "Lithuania"),
    LUXEMBOURG("LU", "Luxembourg"),
    LATVIA("LV", "Latvia"),
    LIBYA("LY", "Libya"),
    MOROCCO("MA", "Morocco"),
    MONACO("MC", "Monaco"),
    MOLDOVA("MD", "Moldova"),
    MONTENEGRO("ME", "Montenegro"),
    SAINT_MARTIN("MF", "Saint Martin"),
    MADAGASCAR("MG", "Madagascar"),
    MARSHALL_ISLANDS("MH", "Marshall Islands"),
    MACEDONIA("MK", "Macedonia"),
    MALI("ML", "Mali"),
    MYANMAR("MM", "Myanmar"),
    MONGOLIA("MN", "Mongolia"),
    MACAO("MO", "Macao"),
    NORTHERN_MARIANA_ISLANDS("MP", "Northern Mariana Islands"),
    MARTINIQUE("MQ", "Martinique"),
    MAURITANIA("MR", "Mauritania"),
    MONTSERRAT("MS", "Montserrat"),
    MALTA("MT", "Malta"),
    MAURITIUS("MU", "Mauritius"),
    MALDIVES("MV", "Maldives"),
    MALAWI("MW", "Malawi"),
    MEXICO("MX", "Mexico"),
    MALAYSIA("MY", "Malaysia"),
    MOZAMBIQUE("MZ", "Mozambique"),
    NAMIBIA("NA", "Namibia"),
    NEW_CALEDONIA("NC", "New Caledonia"),
    NIGER("NE", "Niger"),
    NORFOLK_ISLAND("NF", "Norfolk Island"),
    NIGERIA("NG", "Nigeria"),
    NICARAGUA("NI", "Nicaragua"),
    NETHERLANDS("NL", "Netherlands"),
    NORWAY("NO", "Norway"),
    NEPAL("NP", "Nepal"),
    NAURU("NR", "Nauru"),
    NIUE("NU", "Niue"),
    NEW_ZEALAND("NZ", "New Zealand"),
    OMAN("OM", "Oman"),
    PANAMA("PA", "Panama"),
    PERU("PE", "Peru"),
    FRENCH_POLYNESIA("PF", "French Polynesia"),
    PAPUA_NEW_GUINEA("PG", "Papua New Guinea"),
    PHILIPPINES("PH", "Philippines"),
    PAKISTAN("PK", "Pakistan"),
    POLAND("PL", "Poland"),
    SAINT_PIERRE_AND_MIQUELON("PM", "Saint Pierre And Miquelon"),
    PITCAIRN("PN", "Pitcairn"),
    PUERTO_RICO("PR", "Puerto Rico"),
    PALESTINE("PS", "Palestine"),
    PORTUGAL("PT", "Portugal"),
    PALAU("PW", "Palau"),
    PARAGUAY("PY", "Paraguay"),
    QATAR("QA", "Qatar"),
    REUNION("RE", "Reunion"),
    ROMANIA("RO", "Romania"),
    SERBIA("RS", "Serbia"),
    RUSSIA("RU", "Russia"),
    RWANDA("RW", "Rwanda"),
    SAUDI_ARABIA("SA", "Saudi Arabia"),
    SOLOMON_ISLANDS("SB", "Solomon Islands"),
    SEYCHELLES("SC", "Seychelles"),
    SUDAN("SD", "Sudan"),
    SWEDEN("SE", "Sweden"),
    SINGAPORE("SG", "Singapore"),
    SAINT_HELENA("SH", "Saint Helena"),
    SLOVENIA("SI", "Slovenia"),
    SVALBARD_AND_JAN_MAYEN("SJ", "Svalbard And Jan Mayen"),
    SLOVAKIA("SK", "Slovakia"),
    SIERRA_LEONE("SL", "Sierra Leone"),
    SAN_MARINO("SM", "San Marino"),
    SENEGAL("SN", "Senegal"),
    SOMALIA("SO", "Somalia"),
    SURINAME("SR", "Suriname"),
    SOUTH_SUDAN("SS", "South Sudan"),
    SAO_TOME_AND_PRINCIPE("ST", "Sao Tome And Principe"),
    EL_SALVADOR("SV", "El Salvador"),
    SINT_MAARTEN_DUTCH_PART("SX", "Sint Maarten (Dutch part)"),
    SYRIA("SY", "Syria"),
    SWAZILAND("SZ", "Swaziland"),
    TURKS_AND_CAICOS_ISLANDS("TC", "Turks And Caicos Islands"),
    CHAD("TD", "Chad"),
    FRENCH_SOUTHERN_TERRITORIES("TF", "French Southern Territories"),
    TOGO("TG", "Togo"),
    THAILAND("TH", "Thailand"),
    TAJIKISTAN("TJ", "Tajikistan"),
    TOKELAU("TK", "Tokelau"),
    TIMOR_LESTE("TL", "Timor-Leste"),
    TURKMENISTAN("TM", "Turkmenistan"),
    TUNISIA("TN", "Tunisia"),
    TONGA("TO", "Tonga"),
    TURKEY("TR", "Turkey"),
    TRINIDAD_AND_TOBAGO("TT", "Trinidad and Tobago"),
    TUVALU("TV", "Tuvalu"),
    TAIWAN("TW", "Taiwan"),
    TANZANIA("TZ", "Tanzania"),
    UKRAINE("UA", "Ukraine"),
    UGANDA("UG", "Uganda"),
    UNITED_STATES_MINOR_OUTLYING_ISLANDS("UM", "United States Minor Outlying Islands"),
    UNITED_STATES("US", "United States"),
    URUGUAY("UY", "Uruguay"),
    UZBEKISTAN("UZ", "Uzbekistan"),
    VATICAN("VA", "Vatican"),
    SAINT_VINCENT_AND_THE_GRENADINES("VC", "Saint Vincent And The Grenadines"),
    VENEZUELA("VE", "Venezuela"),
    BRITISH_VIRGIN_ISLANDS("VG", "British Virgin Islands"),
    U_S__VIRGIN_ISLANDS("VI", "U.S. Virgin Islands"),
    VIETNAM("VN", "Vietnam"),
    VANUATU("VU", "Vanuatu"),
    WALLIS_AND_FUTUNA("WF", "Wallis And Futuna"),
    SAMOA("WS", "Samoa"),
    YEMEN("YE", "Yemen"),
    MAYOTTE("YT", "Mayotte"),
    SOUTH_AFRICA("ZA", "South Africa"),
    ZAMBIA("ZM", "Zambia"),
    ZIMBABWE("ZW", "Zimbabwe");

    companion object {
        fun byIsoTag(isoTag: String): Country? = values().find { it.isoTag == isoTag }
        fun byLocale(locale: Locale): Country? = byIsoTag(locale.country)
    }
}