package org.fsb.FlixFlow.Models;

public class Preferences_film {
	private int id_film;
	private int id_preferences_film;
	private int id_utilisateur;

	public Preferences_film() {
		super();
	}

	public Preferences_film(int id_preferences_film, int id_utilisateur, int id_film) {
		super();
		this.id_preferences_film = id_preferences_film;
		this.id_utilisateur = id_utilisateur;
		this.id_film = id_film;
	}

	public int getId_film() {
		return id_film;
	}

	public int getId_preferences_film() {
		return id_preferences_film;
	}

	public int getId_utilisateur() {
		return id_utilisateur;
	}

	public void setId_film(int id_film) {
		this.id_film = id_film;
	}

	public void setId_preferences_film(int id_preferences_film) {
		this.id_preferences_film = id_preferences_film;
	}

	public void setId_utilisateur(int id_utilisateur) {
		this.id_utilisateur = id_utilisateur;
	}

}
