package net.stenschmidt.desksearch;

import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

class Convert {

	private SimpleDateFormat isoDate;

	public Convert() {
		this.isoDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public Timestamp toTimestamp(Date date) {
		return Timestamp.valueOf(this.isoDate.format(date));
	}

	public Timestamp toTimestamp(FileTime fileTime) {
		return this.toTimestamp(this.toDate(fileTime));
	}

	public Date toDate(FileTime fileTime) {
		return new Date(fileTime.toMillis());
	}
}