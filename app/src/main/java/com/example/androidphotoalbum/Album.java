package com.example.androidphotoalbum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jasmine Buttar
 * @author Zalak Shingala
 */
public class Album implements Serializable {

	private static final long serialVersionUID = 1L;

	String albumName;
	HashMap<String, Photo> photos;

	public Album(String albumName) {
		super();
		this.albumName = albumName;
		this.photos = new HashMap<String, Photo>();
	}

	public String getAlbumName()
	{
		return albumName;
	}

	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}

	public HashMap<String, Photo> getPhotos()
	{
		return photos;
	}

	public void setPhotos(HashMap<String, Photo> photos)
	{
		this.photos = photos;
	}

	public String firstPhoto()
	{
		HashMap<String, Photo> map = getPhotos();
		for (Map.Entry<String, Photo> entries : map.entrySet())
		{
			return entries.getValue().filename;
		}
		return null;
	}

	/**
	 * Adds photo to the respective Album
	 *
	 * @param p photo to add
	 * @param a album in which photo is being added
	 * @param filename name of the specification
	 * @return true if photo is added, false if photo already exists in the album
	 */
	public boolean addPhotoToAlbum(Photo p, Album a, String filename) {
		if (!photos.containsValue(p))
		{
			p.setParentAlbum(filename);
			a.photos.put(filename, p);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes a photo p from the respective Album
	 *
	 * @param p photo to be deleted
	 * @param a album from which the photo is being deleted
	 * @return true if photo is removed,  false if photo does not exist in the album
	 */
	public boolean deletePhotoFromAlbum(Photo p, Album a) {

		if (a.photos.containsValue(p)) {
			a.photos.remove(p);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int output = 1;
		output = prime * output + ((albumName == null) ? 0 : albumName.hashCode());
		output = prime * output + ((photos == null) ? 0 : photos.hashCode());
		return output;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (albumName == null) {
			if (other.albumName != null)
				return false;
		} else if (!albumName.equals(other.albumName))
			return false;
		if (photos == null) {
			if (other.photos != null)
				return false;
		} else if (!photos.equals(other.photos))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Album [albumName=" + albumName + ", photos=" + photos + "]";
	}
}
