package kz.ne.railways.tezcustoms.service.entity.asudkr;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(schema = "KTZ", name = "NE_SMGS_DESTINATION_PLACE_INFO")
public class NeSmgsDestinationPlaceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEST_PLACE_INFO_UN")
    private Long destPlaceInfoUn;

    @Column(name = "INVOICE_UN")
    private Long invoiceUn;

    @Column(name = "DEST_PLACE", length = 1024)
    private String destPlace;

    @Column(name = "DEST_PLACE_STA", length = 6)
    private String destPlaceSta;

    @Column(name = "DEST_PLACE_COUNTRY_CODE", length = 16)
    private String destPlaceCountryCode;

    @Column(name = "DEST_PLACE_INDEX", length = 64)
    private String destPlaceIndex;

    @Column(name = "DEST_PLACE_CITY", length = 512)
    private String destPlaceCity;

    @Column(name = "DEST_PLACE_REGION", length = 200)
    private String destPlaceRegion;

    @Column(name = "DEST_PLACE_STREET", length = 512)
    private String destPlaceStreet;

    @Column(name = "DEST_PLACE_BUILDING", length = 512)
    private String destPlaceBuilding;

    @Column(name = "DEST_PLACE_CUSTOM_ORG_UN")
    private Long destPlaceCustomOrgUn;

    @Column(name = "DEST_PLACE_CUSTOM_CODE", length = 10)
    private String destPlaceCustomCode;

    @Column(name = "DEST_PLACE_CUSTOM_NAME", length = 250)
    private String destPlaceCustomName;

}
