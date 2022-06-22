package kz.ne.railways.tezcustoms.service.model;

public class ContainerData {
    private Long containerListUn;
    private String NumContainer;
    private Integer containerFilled;
    private Long vagonAccessory;// принадлежность контейнера
    private String containerMark;
    private String containerCode;// Типоразмер контейнера

    private String containerCodeName;
    private String vagonAccessoryName;

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getNumContainer() {
        return NumContainer;
    }

    public void setNumContainer(String numContainer) {
        NumContainer = numContainer;
    }

    public Integer getContainerFilled() {
        return containerFilled;
    }

    public void setContainerFilled(Integer containerFilled) {
        this.containerFilled = containerFilled;
    }

    public Long getVagonAccessory() {
        return vagonAccessory;
    }

    public void setVagonAccessory(Long vagonAccessory) {
        this.vagonAccessory = vagonAccessory;
    }

    public String getContainerMark() {
        return containerMark;
    }

    public void setContainerMark(String containerMark) {
        this.containerMark = containerMark;
    }

    public String getContainerCodeName() {
        return containerCodeName;
    }

    public void setContainerCodeName(String containerCodeName) {
        this.containerCodeName = containerCodeName;
    }

    public String getVagonAccessoryName() {
        return vagonAccessoryName;
    }

    public void setVagonAccessoryName(String vagonAccessoryName) {
        this.vagonAccessoryName = vagonAccessoryName;
    }

    public Long getContainerListUn() {
        return containerListUn;
    }

    public void setContainerListUn(Long containerListUn) {
        this.containerListUn = containerListUn;
    }
}
