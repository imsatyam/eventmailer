package com.satyam.mailer.job;

import com.satyam.app.util.base.lang.Assert;
import com.satyam.mailer.model.Configuration;
import com.satyam.mailer.service.ApplicationUtil;
import com.satyam.mailer.service.JobProcessor;
import com.satyam.mailer.service.JobProcessorFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Satyam on 9/10/2016.
 */
public class JobRunner {

    private static final Logger LOG = LoggerFactory.getLogger(JobRunner.class);

    public static void main(String[] args) {
        LOG.info("Starting the mailer job.");
        long startTime = System.currentTimeMillis();

        try {

            if (args.length < 1) {
                throw new IllegalArgumentException("Job configuration file is mandatory.");
            }

            LOG.info("Loading context information...");
            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml");

            Configuration configuration = ApplicationUtil.getConfiguration(args[0]);
            Assert.notNull(configuration, "Configuration cannot be null. Please correct the configuration file.");
            Assert.notNull(configuration.getApplication(), "Application information cannot be null. Please correct the configuration file.");
            Assert.notNull(configuration.getRepository(), "Repository information cannot be null. Please correct the configuration file.");
            Assert.notNull(configuration.getUser(), "User information cannot be null. Please correct the configuration file.");

            LOG.info("Getting job processor...");
            JobProcessorFactory jobProcessorFactory = (JobProcessorFactory)ctx.getBean("jobProcessorFactory");
            String jobProcessorId = StringUtils.join(configuration.getApplication().getApplicationId(), "Processor");
            JobProcessor jobProcessor = jobProcessorFactory.getJobProcessor(jobProcessorId);
            jobProcessor.processJob(configuration);

            LOG.info("Job complete. Total time taken : [{} ms].", System.currentTimeMillis() - startTime);

        } catch (Exception exception) {
            LOG.error("Exception in mailer job: ", exception);
            System.exit(1);
        }
    }
}
