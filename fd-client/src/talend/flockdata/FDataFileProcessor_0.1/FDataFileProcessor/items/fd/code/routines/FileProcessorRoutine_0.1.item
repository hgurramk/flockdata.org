package routines;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.flockdata.helper.FlockDataJsonFactory;
import org.flockdata.helper.FlockException;
import org.flockdata.helper.NotFoundException;
import org.flockdata.model.Company;
import org.flockdata.profile.model.Mappable;
import org.flockdata.profile.model.ProfileConfiguration;
import org.flockdata.registration.bean.TagInputBean;
import org.flockdata.track.bean.ContentInputBean;
import org.flockdata.track.bean.EntityLinkInputBean;
import org.flockdata.track.bean.EntityInputBean;
import org.flockdata.transform.ClientConfiguration;
import org.flockdata.transform.FdLoader;
import org.flockdata.transform.FdWriter;
import org.flockdata.transform.FileProcessor;
import org.flockdata.transform.TransformationHelper;
import org.flockdata.transform.Transformer;
import org.flockdata.transform.csv.CsvEntityMapper;
import org.flockdata.transform.tags.TagMapper;
import org.flockdata.transform.xml.XmlMappable;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

/*
 * user specification: the function's comment should contain keys as follows: 1. write about the function's comment.but
 * it must be before the "{talendTypes}" key.
 * 
 * 2. {talendTypes} 's value must be talend Type, it is required . its value should be one of: String, char | Character,
 * long | Long, int | Integer, boolean | Boolean, byte | Byte, Date, double | Double, float | Float, Object, short |
 * Short
 * 
 * 3. {Category} define a category for the Function. it is required. its value is user-defined .
 * 
 * 4. {param} 's format is: {param} <type>[(<default value or closed list values>)] <name>[ : <comment>]
 * 
 * <type> 's value should be one of: string, int, list, double, object, boolean, long, char, date. <name>'s value is the
 * Function's parameter name. the {param} is optional. so if you the Function without the parameters. the {param} don't
 * added. you can have many parameters for the Function.
 * 
 * 5. {example} gives a example for the Function. it is optional.
 */
public class FileProcessorRoutine<Static> {

    /**
     * helloExample: not return value, only print "hello" + message.
     * 
     * 
     * {talendTypes} String
     * 
     * {Category} User Defined
     * 
     * {param} string("world") input: The string need to be printed.
     * 
     * {example} helloExemple("world") # hello world !.
     */
    public static void helloExample(String message) {
        if (message == null) {
            message = "World"; //$NON-NLS-1$
        }
        System.out.println("Hello " + message + " !"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public static String reverseString(String message) {
    	StringBuffer sb = new StringBuffer();
    	try{
        
        int len = message.length();
        for (int i = len - 1; i >= 0; i--) {
        sb.append(message.charAt(i));
        }
       
        System.out.println("reverse string==========" +sb.toString());
    	}
    	catch(Exception e){
    	 	System.out.println("Exception ======================="+ e.getMessage());
    	 	
    	}
        
    	 return sb.toString();
        }
    
private static final org.slf4j.Logger logger1 = LoggerFactory.getLogger(FileProcessor.class);
    
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Transformer.class);

    public static Mappable getMappable(ProfileConfiguration profile) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Mappable mappable = null;

        if (!(profile.getHandler() == null || profile.getHandler().equals("")))
            mappable = (Mappable) Class.forName(profile.getHandler()).newInstance();
        else if (profile.getTagOrEntity() == ProfileConfiguration.DataType.ENTITY) {
            mappable = CsvEntityMapper.newInstance(profile);
        } else if (profile.getTagOrEntity() == ProfileConfiguration.DataType.TAG) {
            mappable = TagMapper.newInstance(profile);
        } else
            logger.error("Unable to determine the implementing handler");
		return mappable;
		}
    

    private static final DecimalFormat formatter = new DecimalFormat();
    private static FdLoader fdLoader;
    private static long skipCount;
    private static long rowsToProcess = 0;
    
    public static Long processFile(ProfileConfiguration importProfile, String source, FdWriter writer, Company company, ClientConfiguration defaults) throws IllegalAccessException, InstantiationException, IOException, FlockException, ClassNotFoundException {

        fdLoader = new FdLoader(importProfile, writer, defaults, company);

        //String source = path;
        logger.info("Start processing of {}", source);
        Collection<String> files = resolveFiles(source);
        long result = 0;
        try {
            for (String file : files) {

                if (importProfile.getContentType() == ProfileConfiguration.ContentType.CSV)
                    result = processCSVFile(file, importProfile, writer);
                else if (importProfile.getContentType() == ProfileConfiguration.ContentType.XML)
                    result = processXMLFile(file, importProfile, writer);
                else if (importProfile.getContentType() == ProfileConfiguration.ContentType.JSON) {
                    if (importProfile.getTagOrEntity() == ProfileConfiguration.DataType.ENTITY)
                        result = processJsonEntities(file, importProfile, writer);
                    else
                        result = processJsonTags(file);
                }

            }
        } finally {
            if (result > 0) {
                writer.close(fdLoader);
            }
        }

        logger.info("Processed {}", source);
        return result;
    }
    
    private static long processJsonTags(String fileName) throws FlockException {
        Collection<TagInputBean> tags;
        ObjectMapper mapper = FlockDataJsonFactory.getObjectMapper();
        long processed = 0;
        try {
            File file = new File(fileName);
            InputStream stream = null;
            if (!file.exists()) {
                // Try as a resource
                stream = ClassLoader.class.getResourceAsStream(fileName);
                if (stream == null) {
                    logger.error("{} does not exist", fileName);
                    return 0;
                }
            }
            TypeFactory typeFactory = mapper.getTypeFactory();
            CollectionType collType = typeFactory.constructCollectionType(ArrayList.class, TagInputBean.class);

            if (file.exists())
                tags = mapper.readValue(file, collType);
            else
                tags = mapper.readValue(stream, collType);
            for (TagInputBean tag : tags) {
                fdLoader.batchTag(tag, "JSON Tag Importer");
                processed++;
            }

        } catch (IOException e) {
            logger.error("Error writing exceptions with {} [{}]", fileName, e.getMessage());
            throw new RuntimeException("IO Exception ", e);
        } finally {
            if (processed > 0l)
                fdLoader.flush();

        }
        return tags.size();
    }

    private static long processJsonEntities(String fileName, ProfileConfiguration importProfile, FdWriter writer) throws FlockException {
        long rows = 0;

        File file = new File(fileName);
        InputStream stream = null;
        if (!file.exists()) {
            // Try as a resource
            stream = ClassLoader.class.getResourceAsStream(fileName);
            if (stream == null) {
                logger.error("{} does not exist", fileName);
                return 0;
            }
        }
        StopWatch watch = new StopWatch();

        JsonFactory jfactory = new JsonFactory();
        JsonParser jParser;
        List<EntityLinkInputBean> referenceInputBeans = new ArrayList<>();

        try {

            //String docType = mappable.getDataType();
            watch.start();
            ObjectMapper om =FlockDataJsonFactory.getObjectMapper();
            try {

                if (stream != null)
                    jParser = jfactory.createParser(stream);
                else
                    jParser = jfactory.createParser(file);


                JsonToken currentToken = jParser.nextToken();
                long then = new DateTime().getMillis();
                JsonNode node;
                if (currentToken == JsonToken.START_ARRAY || currentToken == JsonToken.START_OBJECT) {
                    while (currentToken != null && currentToken != JsonToken.END_OBJECT) {

                        while (currentToken != null && jParser.nextToken() != JsonToken.END_ARRAY) {
                            node = om.readTree(jParser);
                            if (node != null) {
                                processJsonNode(node, importProfile, referenceInputBeans);
                                if (stopProcessing(rows++, then)) {
                                    break;
                                }

                            }
                            currentToken = jParser.nextToken();

                        }
                    }
                } else if (currentToken == JsonToken.START_OBJECT) {

                    //om.readTree(jParser);
                    node = om.readTree(jParser);
                    processJsonNode(node, importProfile, referenceInputBeans);
                }

            } catch (IOException e1) {
                logger.error("Unexpected", e1);
            }


        } finally {
            writer.close(fdLoader);
        }
        if (!referenceInputBeans.isEmpty()) {
            logger.debug("Wrote [{}] cross references",
                    writeEntityLinks(writer, referenceInputBeans));
        }

        return endProcess(watch, rows, 0);
    }

    private static void processJsonNode(JsonNode node, ProfileConfiguration importProfile, List<EntityLinkInputBean> referenceInputBeans) throws FlockException {
        EntityInputBean entityInputBean = Transformer.transformToEntity(node, importProfile);
        if (!entityInputBean.getEntityLinks().isEmpty()) {
            referenceInputBeans.add(new EntityLinkInputBean(entityInputBean));
            entityInputBean.getEntityLinks().size();
        }

        fdLoader.batchEntity(entityInputBean);

    }

    private static long processXMLFile(String file, ProfileConfiguration importProfile, FdWriter writer) throws IOException, FlockException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        try {
            long rows = 0;
            StopWatch watch = new StopWatch();
            StreamSource source = new StreamSource(file);
            XMLInputFactory xif = XMLInputFactory.newFactory();
            XMLStreamReader xsr = xif.createXMLStreamReader(source);
            List<EntityLinkInputBean> referenceInputBeans = new ArrayList<>();
            XmlMappable mappable = (XmlMappable) Transformer.getMappable(importProfile);
            mappable.positionReader(xsr);

            String dataType = mappable.getDataType();
            watch.start();
            try {
                long then = new DateTime().getMillis();
                while (xsr.getLocalName().equals(dataType)) {
                    EntityInputBean entityInputBean = Transformer.transformToEntity(mappable, xsr, importProfile);
                    if (entityInputBean!=null && !entityInputBean.getEntityLinks().isEmpty()) {
                        referenceInputBeans.add(new EntityLinkInputBean(entityInputBean));
                        entityInputBean.getEntityLinks().size();
                    }
                    rows++;
                    xsr.nextTag();
                    fdLoader.batchEntity(entityInputBean);

                    if (stopProcessing(rows, then)) {
                        break;
                    }

                }
            } finally {
                writer.close(fdLoader);
            }
            if (!referenceInputBeans.isEmpty()) {
                logger.debug("Wrote [{}] cross references", writeEntityLinks(writer, referenceInputBeans));
            }
            return endProcess(watch, rows, 0);


        } catch (XMLStreamException | JAXBException e1) {
            throw new IOException(e1);
        }
    }

    
    public static long processCSVFile(String file, ProfileConfiguration importProfile, FdWriter writer) throws IOException, IllegalAccessException, InstantiationException, FlockException, ClassNotFoundException {

        StopWatch watch = new StopWatch();


        long ignoreCount = 0;
        long currentRow = 0;

        BufferedReader br;
        Reader fileObject = getReader(file);

        br = new BufferedReader(fileObject);
        List<EntityLinkInputBean> referenceInputBeans = new ArrayList<>();

        try {
            CSVReader csvReader;
            if (importProfile.getQuoteCharacter() != null)
                csvReader = new CSVReader(br, importProfile.getDelimiter(), importProfile.getQuoteCharacter().charAt(0));
            else
                csvReader = new CSVReader(br, importProfile.getDelimiter());

            String[] headerRow = null;
            String[] nextLine;
            if (importProfile.hasHeader()) {
                while ((nextLine = csvReader.readNext()) != null) {
                    if (!nextLine[0].equals("")) {
                        if (!(((nextLine[0].charAt(0) == '#') || nextLine[0].charAt(1) == '#'))) {
                            headerRow = nextLine;
                            break;
                        }
                    }
                }
            }
            watch.start();

            if (skipCount > 0)
                logger1.info("Skipping first {} rows", skipCount);

            long then = System.currentTimeMillis();

            while ((nextLine = csvReader.readNext()) != null) {
                if (!ignoreRow(nextLine)) {
                    if (headerRow == null) {
                        headerRow = TransformationHelper.defaultHeader(nextLine, importProfile);
                    }
                    currentRow++;
                    if (currentRow >= skipCount) {
                        if (currentRow == skipCount)
                            logger1.info("Processing now begins at row {}", skipCount);

                        nextLine = preProcess(nextLine, importProfile);

                        Map<String, Object> map = TransformationHelper.convertToMap(headerRow, nextLine, importProfile);

                        if (map != null) {
                            if (importProfile.getTagOrEntity() == ProfileConfiguration.DataType.ENTITY) {
                                EntityInputBean entityInputBean = Transformer.transformToEntity(map, importProfile);
                                if (entityInputBean != null && !entityInputBean.getEntityLinks().isEmpty()) {
                                    referenceInputBeans.add(new EntityLinkInputBean(entityInputBean));
                                    currentRow = currentRow + entityInputBean.getEntityLinks().size();
                                }
                                // Dispatch/load mechanism
                                if ( entityInputBean!=null )
                                    fdLoader.batchEntity(entityInputBean);
                            } else {// Tag
                                TagInputBean tagInputBean = Transformer.transformToTag(map, importProfile);
                                if (tagInputBean != null) {
                                    fdLoader.batchTag(tagInputBean, "TagInputBean");
                                }
                            }
                            if (stopProcessing(currentRow, then)) {
                                break;
                            }
                        }
                    }
                } else {
                    ignoreCount++;
                }
            }
        } finally {

            writer.close(fdLoader);

            if (!referenceInputBeans.isEmpty()) {
                // ToDo: This approach is un-scalable - routine works but the ArrayList is kept in memory. It's ok for now...
                logger1.debug("Wrote [{}] x entity links", writeEntityLinks(writer, referenceInputBeans));
            }
            br.close();
        }

        return endProcess(watch, currentRow, ignoreCount);
    }
    
    public static int writeEntityLinks(FdWriter fdWriter, List<EntityLinkInputBean> referenceInputBeans) throws FlockException {
        return fdWriter.flushEntityLinks(referenceInputBeans);
    }
    
    public boolean stopProcessing(long currentRow) {
        return stopProcessing(currentRow, 0l);
    }

    public static boolean stopProcessing(long currentRow, long then) {
        //DAT-350

        if (rowsToProcess == 0) {
            if (currentRow % 1000 == 0)
                logger1.info("Processed {} elapsed seconds {}", currentRow - skipCount, (new DateTime().getMillis() - then) / 1000d);
            return false;
        }
        boolean stop = currentRow >= skipCount + rowsToProcess;

        if (!stop && currentRow != skipCount && then > 0 && currentRow % 1000 == 0)
            logger1.info("Processed {} elapsed seconds {}", currentRow - skipCount, (new DateTime().getMillis() - then) / 1000d);

        if (currentRow <= skipCount)
            return false;

        if (stop)
            logger1.info("Process stopping after the {} requested rows.", rowsToProcess);

        return stop;
    }

    
    public static boolean ignoreRow(String[] nextLine) {
        return nextLine[0].startsWith("#");
    }

    static StandardEvaluationContext context = new StandardEvaluationContext();

    public static String[] preProcess(String[] row, ProfileConfiguration importProfile) {
        String[] result = new String[row.length];
        String exp = importProfile.getPreParseRowExp();
        if ((exp == null || exp.equals("")))
            return row;
        int i = 0;
        for (String column : row) {

            Object value = evaluateExpression(column, exp);
            result[i] = value.toString();
            i++;


        }
        return result;
    }

    public static final ExpressionParser parser = new SpelExpressionParser();

    public static Object evaluateExpression(Object value, String expression) {
        context.setVariable("value", value);
        return parser.parseExpression(expression).getValue(context);
    }

    public static Reader getReader(String file) throws NotFoundException {
        InputStream stream = ClassLoader.class.getResourceAsStream(file);

        Reader fileObject = null;
        try {
            fileObject = new FileReader(file);
        } catch (FileNotFoundException e) {
            if (stream != null)
                fileObject = new InputStreamReader(stream);

        }
        if (fileObject == null) {
            logger1.error("Unable to resolve the source [{}]", file);
            throw new NotFoundException("Unable to resolve the source " + file);
        }
        return fileObject;
    }

    public static long endProcess(StopWatch watch, long rows, long ignoreCount) {
        watch.stop();
        double mins = watch.getTotalTimeSeconds() / 60;
        long rowsProcessed = rows - skipCount;
        if (skipCount > 0)
            logger1.info("Completed [{}] rows in [{}] secs. rpm [{}]. Skipped first [{}] rows, ignored [{}] rows. Finished on row {}", rowsProcessed, formatter.format(watch.getTotalTimeSeconds()), formatter.format(rowsProcessed / mins), skipCount, ignoreCount, rows);
        else
            logger1.info("Completed [{}] rows in [{}] secs. rpm [{}] Ignored [{}] rows. Finished on row [{}]", rowsProcessed, formatter.format(watch.getTotalTimeSeconds()), formatter.format(rowsProcessed / mins), ignoreCount, rows);
        return rows;
    }

    public static boolean validateArgs(String pathToBatch) throws NotFoundException, IOException {
        Reader reader = getReader(pathToBatch);
        if (reader != null)
            reader.close();
        return true;
    }

    // routine for transformer.class
    
    
    
    public static EntityInputBean transformToEntity(Map<String, Object> row, ProfileConfiguration importProfile) throws FlockException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Mappable mappable = getMappable(importProfile);
        Map<String, Object> jsonData = mappable.setData(row, importProfile);
        if ( jsonData == null )
            return null;// No entity did not get created

        EntityInputBean entityInputBean = (EntityInputBean) mappable;

        if (importProfile.isEntityOnly() || jsonData.isEmpty()) {
            entityInputBean.setEntityOnly(true);
            // It's all Meta baby - no log information
        } else {
            String updatingUser = entityInputBean.getUpdateUser();
            if (updatingUser == null)
                updatingUser = (entityInputBean.getFortressUser() == null ? importProfile.getFortressUser() : entityInputBean.getFortressUser());

            ContentInputBean contentInputBean = new ContentInputBean(updatingUser, (entityInputBean.getWhen() != null ? new DateTime(entityInputBean.getWhen()) : null), jsonData);
            contentInputBean.setEvent(importProfile.getEvent());
            entityInputBean.setContent(contentInputBean);
        }
        return entityInputBean;

    }

    // Routine for tagInputBean
    
    public static TagInputBean transformToTag(Map<String, Object> row, ProfileConfiguration importProfile) throws FlockException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Mappable mappable = getMappable(importProfile);
        mappable.setData(row, importProfile);
        return (TagInputBean) mappable;
    }
    
    public static Collection<String> resolveFiles(String source) throws IOException, NotFoundException {
        ArrayList<String> results = new ArrayList<>();
        boolean absoluteFile = true;
        if (source.contains("*") || source.contains("?") || source.endsWith(File.separator))
            absoluteFile = false;

        if (absoluteFile) {
            Reader reader;
            reader = getReader(source);
            if (reader != null) {
                reader.close();
                results.add(source);
                return results;
            }
        }

        String filter;
        String path;

        Path toResolve = Paths.get(source);

        if (source.endsWith(File.separator))
            filter = "*";
        else
            filter = toResolve.getFileName().toString();
        if (filter == null)
            filter = "*";
        path = source.substring(0, source.lastIndexOf(File.separator) + 1);
        FileFilter fileFilter = new WildcardFileFilter(filter);

        // Split the source in to path and filter.
        //path = source.substring(0, source.indexOf())

        File folder = new UrlResource("file:" + path).getFile();
        File[] listOfFiles = folder.listFiles(fileFilter);
        if (listOfFiles == null) {
            folder = new ClassPathResource(path).getFile();
            listOfFiles = folder.listFiles(fileFilter);
        }

        for (File file : listOfFiles) {
            results.add(file.toString());
        }


        return results;
    }
 }
