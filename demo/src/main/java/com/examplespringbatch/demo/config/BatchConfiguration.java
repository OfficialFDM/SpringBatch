package com.examplespringbatch.demo.config;

import com.examplespringbatch.demo.listener.HwJobExecutionListener;
import com.examplespringbatch.demo.listener.HwStepExecutionListener;
import com.examplespringbatch.demo.model.Product;
import com.examplespringbatch.demo.model.ProductCSV;
import com.examplespringbatch.demo.processor.InMemItemProcessor;
import com.examplespringbatch.demo.reader.InMemReader;
import com.examplespringbatch.demo.reader.ProductServiceAdapter;
import com.examplespringbatch.demo.service.ProductService;
import com.examplespringbatch.demo.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    private JobBuilderFactory jobs;

    private StepBuilderFactory steps;

    private HwJobExecutionListener hwJobExecutionListener;

    private HwStepExecutionListener hwStepExecutionListener;

    private ConsoleItemWriter consoleItemWriter;

    private InMemReader inMemReader;

    private InMemItemProcessor inMemItemProcessor;

    private DataSource dataSource;

    private ProductService productService;

    private ProductServiceAdapter productServiceAdapter;

    private Tasklet helloWorldTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello World");
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step step1() {
        return steps.get("step1")
                .listener(hwStepExecutionListener)
                .tasklet(helloWorldTasklet())
                .build();
    }


    @StepScope
    @Bean
    public StaxEventItemReader xmlItemReader(@Value("#{jobParameters['fileInput']}")
                                                     FileSystemResource inputFile
    ) {
        StaxEventItemReader reader = new StaxEventItemReader();
        //step 1 let reader know where is the file
        reader.setResource(inputFile);

        reader.setFragmentRootElementName("product");

        //tell reader how to parse XML and which domain object to be mapped
        reader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(Product.class);
            }
        });

        return reader;
    }

    @StepScope
    @Bean
    public FlatFileItemReader flatFileItemReader(@Value("#{jobParameters['fileInput']}")
                                                         FileSystemResource inputFile
    ) {
        FlatFileItemReader reader = new FlatFileItemReader();
        //step 1 let reader know where is the file
        reader.setResource(inputFile);

        //create line Mapper
        reader.setLineMapper(new DefaultLineMapper<ProductCSV>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("productId", "productName", "productDesc", "unit", "price");
                        setDelimiter(";");
                    }

                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(ProductCSV.class);
                    }
                });
            }
        });

        //step 3 tell reader to skip the header
        reader.setLinesToSkip(1);
        return reader;
    }

    @StepScope
    @Bean
    public JsonItemReader jsonItemReader(@Value("#{jobParameters['fileInput']}")
                                                 FileSystemResource inputFile
    ) {
        JsonItemReader reader = new JsonItemReader(inputFile, new JacksonJsonObjectReader(Product.class));
        return reader;
    }


    @Bean
    public JdbcCursorItemReader jdbcCursorItemReader() {
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(dataSource);
        //if the name of column mismatches from field's name we use aliases
        reader.setSql("select " +
                "prod_id as productId, prod_name as productName, prod_desc as productDesc, " +
                "unit, price from products");
        reader.setRowMapper(new BeanPropertyRowMapper() {
            {
                setMappedClass(Product.class);
            }

        });
        return reader;
    }

    @Bean
    public ItemReaderAdapter serviceItemReader() {
        ItemReaderAdapter readerAdapter = new ItemReaderAdapter();
        readerAdapter.setTargetObject(productServiceAdapter);
        readerAdapter.setTargetMethod("nextProduct");
        return readerAdapter;
    }

    @Bean
    public Step step2() {
        return steps.get("step2")
                .<Integer, Integer>chunk(3)
                //fileInput=src/main/resources/product.csv
                //.reader(flatFileItemReader(null))
                //.reader(xmlItemReader(null))
                //.reader(jdbcCursorItemReader())
                //.reader(jsonItemReader(null))
                .reader(serviceItemReader())
                .writer(consoleItemWriter)
                .build();
    }

    @Bean
    public Job helloWorldJob() {
        return jobs.get("helloWorldJob")
                .listener(hwJobExecutionListener)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .build();
    }

    //AUTOWIRED SECTION

    @Autowired
    public void setStepBuilderFactory(StepBuilderFactory stepBuilderFactory) {
        this.steps = stepBuilderFactory;
    }

    @Autowired
    public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
        this.jobs = jobBuilderFactory;
    }

    @Autowired
    public void setHwJobExecutionListener(HwJobExecutionListener hwJobExecutionListener) {
        this.hwJobExecutionListener = hwJobExecutionListener;
    }

    @Autowired
    public void setHwStepExecutionListener(HwStepExecutionListener hwStepExecutionListener) {
        this.hwStepExecutionListener = hwStepExecutionListener;
    }

    @Autowired
    public void setConsoleItemWriter(ConsoleItemWriter consoleItemWriter) {
        this.consoleItemWriter = consoleItemWriter;
    }

    @Autowired
    public void setInMemReader(InMemReader inMemReader) {
        this.inMemReader = inMemReader;
    }

    @Autowired
    public void setInMemItemProcessor(InMemItemProcessor inMemItemProcessor) {
        this.inMemItemProcessor = inMemItemProcessor;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setProductServiceAdapter(ProductServiceAdapter productServiceAdapter) {
        this.productServiceAdapter = productServiceAdapter;
    }
}
