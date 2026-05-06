package org.t13.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.SettlementHistory;
import org.t13.app.entity.Transactions;
import org.t13.app.model.DashboardSummary;
import org.t13.app.model.NetSettlementReport;
import org.t13.app.model.TransactionReport;
import org.t13.app.repository.SettlementHistoryRepository;
import org.t13.app.repository.TransactionsRepository;
import org.t13.app.service.ReconciliationService;
import org.t13.app.transformer.TransactionTransformer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ReconciliationServiceImpl implements ReconciliationService {

    private final TransactionsRepository transactionsRepository;
    private final SettlementHistoryRepository settlementHistoryRepository;
    private final Job job;
    private final JobOperator jobOperator;

    public ReconciliationServiceImpl(TransactionsRepository transactionsRepository, SettlementHistoryRepository settlementHistoryRepository, Job job, JobOperator jobOperator) {
        this.transactionsRepository = transactionsRepository;
        this.settlementHistoryRepository = settlementHistoryRepository;
        this.job = job;
        this.jobOperator = jobOperator;
    }

    public HashMap<String,List<Transactions>> getTransactions() {
        List<Transactions> redIssues = transactionsRepository.find().stream()
                .filter(t ->
                        t.getTotalSettledAmount().compareTo(t.getTransactionAmount()) > 0 ||
                        ChronoUnit.DAYS.between(t.getTransactionDate(),
                                t.getLastSettlementDate() == null ? LocalDate.now() : t.getLastSettlementDate() ) > 7)
                .toList();

        List<Transactions> yellowIssues = transactionsRepository.find().stream()
                .filter(t -> t.getTotalSettledAmount().compareTo(t.getTransactionAmount()) < 0 ).toList();

        HashMap<String,List<Transactions>> map = new HashMap<>();
        map.put("redIssues", redIssues);
        map.put("yellowIssues", yellowIssues);

        return map;
    }

    @Override
    @RequestScope
    public void reconcile(MultipartFile file) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("UUID", UUID.randomUUID().toString())
                .addLong("time", System.currentTimeMillis()) // uniqueness
                .toJobParameters();

        log.info("Job parameters : {}", jobParameters);
        log.info("Job name : {}", job.getName());

        JobExecution jobExecution =  jobOperator.start(job, jobParameters);

        log.info("Job executed with id: {}", jobExecution.getId());
        log.info("Job status : {}", jobExecution.getStatus());

        List<NetSettlementReport> netSettlementReports = settlementHistoryRepository.netSettlement();
        netSettlementReports.forEach(transactionsRepository::updateTransactions);
    }

    @Override
    public TransactionReport getTransactionsById(String id) {
        Transactions transactions = transactionsRepository.findById(id).getFirst();
        List<SettlementHistory> settlements = settlementHistoryRepository.getSettlementByLifecyle(transactions.getLifecycleId());
        return TransactionTransformer.transform(transactions, settlements);
    }

    @Override
    public DashboardSummary getDashboardSummary() {

        int NO_SETTLEMENT_AFTER_7_DAYS = transactionsRepository.find().stream()
                .filter(t -> ChronoUnit.DAYS.between(t.getTransactionDate(),
                                        t.getLastSettlementDate() == null ? LocalDate.now() : t.getLastSettlementDate() ) > 7)
                .toList().size();

        DashboardSummary dashboardSummary = transactionsRepository.dashboardReport().getFirst();
        dashboardSummary.setNoSettlementAfter7Days(NO_SETTLEMENT_AFTER_7_DAYS);
        return dashboardSummary;
    }
}
