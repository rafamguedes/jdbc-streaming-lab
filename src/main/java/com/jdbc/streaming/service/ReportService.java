package com.jdbc.streaming.service;

import com.jdbc.streaming.dto.ItemDTO;
import com.jdbc.streaming.repository.ItemReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

  private final ItemReportRepository repository;
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Transactional(readOnly = true)
    public void generateTraditional(
            OutputStream outputStream,
            LocalDateTime startDate,
            LocalDateTime endDate) throws Exception {

        long startTime = System.currentTimeMillis();

        log.info(
                "Memory before query: {} MB",
                usedMemoryMB());

        List<ItemDTO> items =
                repository.findAllItems(
                        startDate,
                        endDate);

        log.info(
                "Memory after query: {} MB",
                usedMemoryMB());

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new OutputStreamWriter(
                                     outputStream,
                                     StandardCharsets.UTF_8))) {

            writeHeader(writer);

            for (ItemDTO item : items) {
                writeCsvLine(writer, item);
            }

            writer.flush();
        }

        long endTime = System.currentTimeMillis();

        log.info(
                """
                Traditional report finished
          
                Records: {}
                Time: {} ms
                Memory: {} MB
                """,
                items.size(),
                (endTime - startTime),
                usedMemoryMB());
    }

  @Transactional(readOnly = true)
  public void generateStreaming(OutputStream outputStream, LocalDateTime startDate, LocalDateTime endDate) throws Exception {

    long startTime = System.currentTimeMillis();

    AtomicLong counter = new AtomicLong();

    outputStream.write(new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

    try (BufferedWriter writer =
        new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

      writeHeader(writer);

      repository.findAllStreamItems(
          item -> {
            long current = counter.incrementAndGet();

            try {
              writeCsvLine(writer, item);

              if (current % 5_000 == 0) {
                writer.flush();
                log.info("Processed: {} | Memory: {} MB", current, usedMemoryMB());
              }

            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          },
          startDate,
          endDate);

      writer.flush();
    }

    long endTime = System.currentTimeMillis();

    log.info(
        """
              Streaming report finished

              Records: {}
              Time: {} ms
              Memory: {} MB
              """,
        counter.get(),
        (endTime - startTime),
        usedMemoryMB());
  }

  private void writeHeader(BufferedWriter writer) throws IOException {

    writer.write(
        "Produto;"
            + "Descrição;"
            + "Quantidade;"
            + "Preço Compra;"
            + "Preço Venda;"
            + "Data Criação;"
            + "Data Atualização;"
            + "Fornecedor;"
            + "Descrição Fornecedor;"
            + "CNPJ;"
            + "E-mail;"
            + "Telefone");

    writer.newLine();
  }

  private void writeCsvLine(BufferedWriter writer, ItemDTO item) throws IOException {

    writeField(writer, escape(item.getItemName()));
    writeField(writer, escape(item.getItemDescription()));
    writeField(writer, String.valueOf(item.getItemQuantity()));
    writeField(writer, String.valueOf(item.getItemBuyPrice()));
    writeField(writer, String.valueOf(item.getItemSellPrice()));
    writeField(writer, formatDate(item.getItemCreatedAt()));
    writeField(writer, formatDate(item.getItemUpdatedAt()));
    writeField(writer, escape(item.getSupplierName()));
    writeField(writer, escape(item.getSupplierDescription()));
    writeField(writer, escape(item.getSupplierCnpj()));
    writeField(writer, escape(item.getSupplierEmail()));

    writer.write(escape(item.getSupplierPhone()));
    writer.newLine();
  }

  private void writeField(BufferedWriter writer, String value) throws IOException {
    writer.write(value);
    writer.write(';');
  }

  private String escape(String value) {
    if (value == null) {
      return "";
    }
    return "\"" + value.replace("\"", "\"\"") + "\"";
  }

  private String formatDate(LocalDateTime date) {
    if (date == null) {
      return "";
    }
    return date.format(DATE_FORMATTER);
  }

  private long usedMemoryMB() {
    Runtime runtime = Runtime.getRuntime();

    return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
  }
}
