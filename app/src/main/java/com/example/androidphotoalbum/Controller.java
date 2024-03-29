package com.example.androidphotoalbum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * @author Jasmine Buttar
 * @author Zalak Shingala
 */
public class Controller
{

	private static Controller instance = null;

	protected HashMap<String, Album> albums;
	public static final String ALBUM_FILE = "albums.dat";
	Context con;

	protected Controller(Context context)
	{
		this.con = context;

		try
		{
			File f = new File(con.getFilesDir() + File.separator + ALBUM_FILE);
			if (f.exists())
			{
				Log.e("File", "Read existing file.");
				read();
			} else
			{
				Log.e("File", "Created new file.");
				File dir = con.getFilesDir();
				if (dir.isDirectory())
				{
					for (File entry : dir.listFiles())
					{
						entry.delete();
					}
				}
				albums = new HashMap<String, Album>();
				write();
			}
		} catch (Exception e)
		{

		}
		return;
	}

	public static Controller getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new Controller(context);
		}
		return instance;
	}

	public void read()
	{
		ObjectInputStream ois;
		try
		{
			ois = new ObjectInputStream(con.openFileInput(ALBUM_FILE));
			albums = (HashMap<String, Album>) ois.readObject();
			ois.close();
			Log.i("File read", "success");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void write()
	{
		ObjectOutputStream os;
		try
		{
			os = new ObjectOutputStream(con.openFileOutput(ALBUM_FILE, Context.MODE_WORLD_WRITEABLE));
			os.writeObject(albums);
			os.close();
			Log.i("File write", "success");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public HashMap<String, Album> getAlbums()
	{
		return this.albums;
	}


	public int getAlbumSize(String album)
	{
		Album a = albums.get(album);
		if (a != null) {
			return a.getPhotos().values().size();
		}
		return 0;
	}

	public int getAlbumSize(Album album)
	{
		if (album != null)
		{
			return album.getPhotos().values().size();
		}
		return 0;
	}

	public HashMap<String, Photo> listPhotosInAlbum(String album) {
		Album a = albums.get(album);
		if (a != null) {
			return a.getPhotos();
		}
		return null;
	}

	public boolean albumExists(String album)
	{
		if (albums.containsKey(album))
		{
			return true;
		}
		return false;
	}

	public boolean createAlbum(String album)
	{
		if (!albums.containsKey(album))
		{
			albums.put(album, new Album(album));
			write();
			return true;
		} else
		{
			return false;
		}
	}


	public boolean deleteAlbum(String album)
	{
		if (albums.containsKey(album))
		{
			albums.remove(album);
			write();
			return true;
		} else
		{
			return false;
		}
	}


	public boolean renameAlbum(String album, String newName)
	{
		Album oldAlbum = albums.get(album);
		if (oldAlbum != null)
		{
			Album newAlbum = new Album(newName);
			HashMap<String, Photo> oldPhotos = oldAlbum.getPhotos();
			HashMap<String, Photo> newPhotos = oldPhotos;
			for (Photo p : oldPhotos.values())
			{
				p.setParentAlbum(album);
			}
			newAlbum.setPhotos(newPhotos);
			albums.put(newName, newAlbum);
			albums.remove(album);
			write();
			return true;
		} else
		{
			return false;
		}
	}


	public boolean addPhotoToAlbum(String filepath, String albumName) throws FileNotFoundException,
			IOException
	{
		if (!new File(filepath).exists())
		{
			throw new FileNotFoundException();
		}


		String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
		File file = new File(con.getFilesDir(), filename);
		if (!file.exists())
		{
			InputStream in = new FileInputStream(filepath);
			OutputStream out = new FileOutputStream(file);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}

		if (!albums.containsKey(albumName))
		{
			return false;
		}
		Photo p = new Photo(filename);
		if (!albums.get(albumName).getPhotos().containsKey(filepath))
		{
			p.setTags(new ArrayList<PhotoTag>());
			p.setParentAlbum(albumName);
			albums.get(albumName).getPhotos().put(filename, p);
			write();
			return true;
		} else
		{
			return false;
		}
	}


	public boolean movePhoto(String filename, String oldAlbumName, String newAlbumName)
	{
		if (!albums.containsKey(oldAlbumName))
		{
			return false;
		}
		if (!albums.containsKey(newAlbumName))
		{
			return false;
		}
		if (albums.get(oldAlbumName).getPhotos().containsKey(filename))
		{
			if (!albums.get(newAlbumName).getPhotos().containsKey(filename))
			{
				albums.get(newAlbumName).getPhotos()
						.put(filename, albums.get(oldAlbumName).getPhotos().get(filename));
			} else
			{
				write();
				return true;
			}
			albums.get(oldAlbumName).getPhotos().remove(filename);
			write();
			return true;
		} else
		{
			return false;
		}
	}

	public boolean removePhotoFromAlbum(String filename, String album)
	{
		if (albums.get(album).getPhotos().containsKey(filename))
		{
			HashMap<String, Photo> allPhotos = getAllPhotos();
			if (!allPhotos.containsKey(filename))
			{
				new File(con.getFilesDir(), filename).delete();
			}
			albums.get(album).getPhotos().remove(filename);
			write();
			return true;
		} else
		{
			return false;
		}
	}

	private HashMap<String, Photo> getAllPhotos()
	{
		HashMap<String, Photo> all = new HashMap<String, Photo>();
		for (Album a : this.albums.values())
		{
			all.putAll(a.getPhotos());
		}
		return all;
	}


	public boolean addTagToPhoto(String album, String filename, String tagType, String tagValue)
	{
		HashMap<String, Photo> photos = this.albums.get(album).getPhotos();
		if (photos.containsKey(filename))
		{

			PhotoTag t = new PhotoTag(tagType, tagValue);
			List<PhotoTag> tags = photos.get(filename).getTags();
			if (!tags.contains(t))
			{
				photos.get(filename).addTag(t);
				write();
				return true;
			} else
			{
				return false;
			}
		} else
		{
			return false;
		}
	}

	public boolean deleteTagFromPhoto(String album, String filename, String tagType, String tagValue)
	{
		Photo photo = this.albums.get(album).getPhotos().get(filename);
		List<PhotoTag> tags = photo.getTags();
		PhotoTag t = new PhotoTag(tagType, tagValue);

		if (tags.isEmpty())
		{
			return false;
		} else if (tags.contains(t))
		{
			tags.remove(t);
			System.out.println("Deleted tag.");
			photo.setTags(tags);
			Album a = this.albums.get(album);
			this.albums.get(album).deletePhotoFromAlbum(photo, a);
			this.albums.get(album).addPhotoToAlbum(photo, a, photo.getFilename());
			write();
			return true;
		} else
		{
			return false;
		}
	}

	public Photo listPhotoInfo(String filename)
	{
		for (Album album : albums.values())
		{
			if (album.getPhotos().containsKey(filename))
			{
				return album.getPhotos().get(filename);
			}
		}
		return null;
	}


	public List<Photo> getPhotosByTag(List<PhotoTag> tags)
	{
		List<Photo> matching = new ArrayList<Photo>();
		for (Album album : albums.values())
		{
			for (Photo photo : album.getPhotos().values())
			{
				List<PhotoTag> photoTags = photo.getTags();
				for (PhotoTag tag : tags) {
					if (tag.getTagType().isEmpty())
					{
						for (PhotoTag t : photoTags)
						{
							if (t.getTagValue().equals(tag.getTagValue()))
							{
								matching.add(photo);
							}
						}
					} else {
						if (photoTags.contains(tag))
						{
							matching.add(photo);
						}
					}
				}
			}
		}
		return matching;
	}


	public List<String> findParentAlbumsOfPhoto(String filename)
	{

		List<String> matching = new ArrayList<String>();
		for (Album album : this.albums.values())
		{
			for (Photo photo : album.getPhotos().values())
			{
				if (photo.getFilename().equalsIgnoreCase(filename))
				{
					matching.add(album.getAlbumName());
				}
			}
		}
		matching.removeAll(Arrays.asList("", null));
		Collections.sort(matching);
		return matching;
	}

	public List<PhotoTag> getTags()
	{

		List<PhotoTag> tags = new ArrayList<PhotoTag>();
		for (Album album : this.albums.values()) {
			for (Photo photo : album.getPhotos().values())
			{
				tags.addAll(photo.getTags());
			}
		}
		return tags;
	}

	public List<PhotoTag> getTagsForPhoto(String album, String filename)
	{
		return this.albums.get(album).getPhotos().get(filename).getTags();
	}

}