package com.RutasMoteras.rutasmoterasapi;

public class RutasModel {
    private int Id;
    private String Title;
    private String Date;
    private String Description;
    private String Comunidad;
    private String TipoMoto;
    private int UserId;
    private String Image;

    public RutasModel(int Id, String Title, String Date, String Description, String Comunidad, String TipoMoto, int UserId, String Image) {
        this.Id = Id;
        this.Title = Title;
        this.Date = Date;
        this.Description = Description;
        this.Comunidad = Comunidad;
        this.TipoMoto = TipoMoto;
        this.UserId = UserId;
        this.Image = Image;
    }

    public RutasModel() {

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getComunidad() {
        return Comunidad;
    }

    public void setComunidad(String comunidad) {
        Comunidad = comunidad;
    }

    public String getTipoMoto() {
        return TipoMoto;
    }

    public void setTipoMoto(String tipoMoto) {
        TipoMoto = tipoMoto;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
