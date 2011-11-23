package be.ugent.intec.gtfsfilter;

import java.io.File;
import java.io.IOException;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.serialization.GtfsWriter;
import org.onebusaway.gtfs.services.GtfsDao;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements Runnable {
	private final Logger LOG = LoggerFactory.getLogger(Main.class);

	private final GtfsReader gtfsReader;
	private final GtfsMutableRelationalDao dao;

	public Main(File input) {

		dao = new GtfsRelationalDaoImpl();

		gtfsReader = new GtfsReader();
		gtfsReader.setEntityStore(dao);
	
		try {
			gtfsReader.setInputLocation(input);
		} catch (IOException e) {
			LOG.error("Unable to process inputfile", e);
		}
	}

	public void run() {

		// read all the data
		try {
			gtfsReader.run();
		} catch (IOException e) {
			LOG.error("Error while processing GTFS-feed", e);
		}

		// filter the content of the dao
		GtfsDao filteredDao = new TramDaoFilter(dao);
		// write the filtered data

		GtfsWriter writer = new GtfsWriter();
		writer.setOutputLocation(new File("output/"));
		try {
			writer.run(filteredDao);
		} catch (IOException e) {
			LOG.error("Error while writing GTFS-feed", e);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "c:/Datasets/DeLijn/gtfs.zip";
		if (args.length > 0)
			fileName = args[0];

		new Main(new File(fileName)).run();
	}

}
