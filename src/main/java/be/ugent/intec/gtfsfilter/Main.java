package be.ugent.intec.gtfsfilter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.serialization.GtfsWriter;
import org.onebusaway.gtfs.services.GtfsDao;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class Main {

	private final static Logger LOG = LoggerFactory.getLogger(Main.class);

	// CLI constants
	private static final String DESCRIPTION_OPT_OUTPUT = "Output location for the filtered gtfs-files (defaults to \"output/\"";
	private static final String DESCRIPTION_OPT_TRANSPORTTYPE = "only keep trips with the given transport types. Possible values are: tram, subway, rail, bus, ferry, cablecar, gondola, funicular";
	private static final String DESCRIPTION_OPT_TIME = "filter trips outside the given timespan (format: yyyy-mm-dd)";
	private static final String DESCRIPTION_OPT_LOCATION = "filter locations outside given latlon-box";

	private static final char LOCATION_OPTION = 'l';
	private static final char TIME_OPTION = 'd';
	private static final char TYPE_OPTION = 't';
	private static final char OUTPUT_OPTION = 'o';

	private static final String USAGE = "[-o <folder>] [-l <lat:lon:lat:lon>] [-d <date>|<start:end>] [-t <types>] INPUT";
	private static final String HEADER = "gtfs-filter - This application can filter GTFS-feed on three different ways: by location, by traveldate and by transporttype";
	private static final String FOOTER = "For more information, see https://github.com/twalcari/gtfs-filter";

	// other constants
	private static final String DEFAULT_OUTPUT_LOCATION = "output/";

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final ImmutableMap<String, Integer> TRANSPORT_TYPES;

	static {
		TRANSPORT_TYPES = new ImmutableMap.Builder<String, Integer>()
				.put("tram", 0).put("subway", 1).put("rail", 2).put("bus", 3)
				.put("ferry", 4).put("cablecar", 5).put("gondola", 6)
				.put("funicular", 7).build();
	}

	private final File input, output;

	private GtfsDao filteredDao = null;

	public Main(File input, File output) {
		this.input = input;
		this.output = output;

	}

	public synchronized void read() {
		if (filteredDao != null)
			throw new IllegalStateException("Reading has already finished");

		GtfsMutableRelationalDao dao = new GtfsRelationalDaoImpl();
		GtfsReader gtfsReader = new GtfsReader();
		gtfsReader.setEntityStore(dao);

		try {
			gtfsReader.setInputLocation(input);
			gtfsReader.run();
			filteredDao = dao;

		} catch (IOException e) {
			LOG.error("Error while processing GTFS-feed", e);
		}
	}

	public void applyLocationFilter(double minlat, double minlon,
			double maxlat, double maxlon) {
		filteredDao = new LocationDaoFilter(filteredDao, minlat, minlon,
				maxlat, maxlon);
	}

	public void applyTimespanFilter(ServiceDate start, ServiceDate end) {
		filteredDao = new TimespanDaoFilter(filteredDao, start, end);
	}

	public void applyTimespanFilter(ServiceDate oneday) {
		filteredDao = new TimespanDaoFilter(filteredDao, oneday);
	}

	public void applyTransportTypeFilter(int... transportTypes) {
		filteredDao = new TransportTypeDaoFilter(filteredDao, transportTypes);
	}

	public synchronized void write() {
		GtfsWriter writer = new GtfsWriter();
		writer.setOutputLocation(output);
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

		Options options = createOptions();

		if (args.length == 0) {
			// show the options
			showUsage(options);

		} else {
			CommandLineParser parser = new PosixParser();
			try {
				CommandLine result = parser.parse(options, args);

				String[] arguments = result.getArgs();

				if (arguments.length < 1)
					throw new ParseException(
							"Input file/folder is a required argument");

				File inputLocation = new File(arguments[0]);

				File outputLocation = new File(result.getOptionValue(
						OUTPUT_OPTION, DEFAULT_OUTPUT_LOCATION));

				Main main = new Main(inputLocation, outputLocation);

				System.out.println("Reading the input GTFS-feed");
				main.read();

				if (result.hasOption(LOCATION_OPTION)) {

					String[] boundaries = result
							.getOptionValues(LOCATION_OPTION);

					System.out.println("Applying location filter");

					LOG.info(
							"Applying location filter with restrictions: {},{} --> {}, {}",
							boundaries);

					main.applyLocationFilter(Double.parseDouble(boundaries[0]),
							Double.parseDouble(boundaries[1]),
							Double.parseDouble(boundaries[2]),
							Double.parseDouble(boundaries[3]));
				}

				if (result.hasOption(TIME_OPTION)) {
					String[] times = result.getOptionValues(TIME_OPTION);

					Date start = DATE_FORMAT.parse(times[0]);

					if (times.length == 1) {
						LOG.info("Applying time filter for one day: {}",
								new ServiceDate(start));
						main.applyTimespanFilter(new ServiceDate(start));
					} else {
						Date end = DATE_FORMAT.parse(times[1]);

						LOG.info(
								"Applying time filter for timespan: {} --> {}",
								new ServiceDate(start), new ServiceDate(end));

						main.applyTimespanFilter(new ServiceDate(start),
								new ServiceDate(end));
					}
				}

				if (result.hasOption(TYPE_OPTION)) {
					String[] types = result.getOptionValues(TYPE_OPTION);

					int[] typeInts = new int[types.length];
					for (int i = 0; i < types.length; i++)
						typeInts[i] = TRANSPORT_TYPES.get(types[i]
								.toLowerCase());

					LOG.info("Applying transport type filters: {}", Arrays.toString(typeInts));
					
					main.applyTransportTypeFilter(typeInts);
				}

				main.write();
			} catch (NumberFormatException | ParseException
					| java.text.ParseException e) {
				System.err
						.println("Parsing failed.  Reason: " + e.getMessage());
				showUsage(options);
			}

		}

	}

	private static void showUsage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
	}

	private static Options createOptions() {

		// filters
		OptionBuilder.withArgName("lat:lon:lat:lon");
		OptionBuilder.withLongOpt("location");
		OptionBuilder.withDescription(DESCRIPTION_OPT_LOCATION);
		OptionBuilder.hasArgs(4);
		OptionBuilder.withValueSeparator(':');

		Option locationOption = OptionBuilder.create(LOCATION_OPTION);

		OptionBuilder.withArgName("start:end");
		OptionBuilder.withLongOpt("timespan");
		OptionBuilder.withDescription(DESCRIPTION_OPT_TIME);
		OptionBuilder.hasArgs(2);
		OptionBuilder.withValueSeparator(':');
		Option timespanOption = OptionBuilder.create(TIME_OPTION);

		OptionBuilder.withArgName("types");
		OptionBuilder.withLongOpt("type");
		OptionBuilder.withDescription(DESCRIPTION_OPT_TRANSPORTTYPE);
		OptionBuilder.hasArgs();
		OptionBuilder.withValueSeparator(',');
		Option typeOption = OptionBuilder.create(TYPE_OPTION);

		// output folder
		OptionBuilder.withLongOpt("output");
		OptionBuilder.withDescription(DESCRIPTION_OPT_OUTPUT);
		OptionBuilder.withArgName("location");
		OptionBuilder.hasArg();
		Option outputOption = OptionBuilder.create(OUTPUT_OPTION);

		Options options = new Options();
		options.addOption(outputOption);
		options.addOption(locationOption);
		options.addOption(timespanOption);
		options.addOption(typeOption);

		return options;
	}
}
