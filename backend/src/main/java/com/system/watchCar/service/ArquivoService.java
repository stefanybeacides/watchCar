package com.system.watchCar.service;

import com.monitorjbl.xlsx.StreamingReader;
import com.system.watchCar.dto.ArquivoDTO;
import com.system.watchCar.dto.DenunciaRequest;
import com.system.watchCar.entity.Artigo;
import com.system.watchCar.entity.Ocorrencia;
import com.system.watchCar.repository.ArtigoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArquivoService {

    private final OcorrenciaService ocorrenciaService;
    private final ArtigoRepository artigoRepository;

    public ArquivoService(OcorrenciaService ocorrenciaService, ArtigoRepository artigoRepository) {
        this.ocorrenciaService = ocorrenciaService;
        this.artigoRepository = artigoRepository;
    }


    public String importarXlsx(MultipartFile file) {
        try {
            IOUtils.setByteArrayMaxOverride(200000000);

            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return "O arquivo enviado não é do tipo XLSX.";
            }

            try (InputStream inputStream = file.getInputStream()) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                // Verificando os cabeçalhos para garantir que as colunas estão corretas
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    return "Não foi possível encontrar cabeçalhos na planilha.";
                }

                List<ArquivoDTO> arquivos = new ArrayList<>();
                System.out.println("Última linha: " + sheet.getLastRowNum());

                int limite = Math.min(sheet.getLastRowNum(), 15);
                for (int i = 1; i <= limite; i++) {
                    //for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    ArquivoDTO dto = new ArquivoDTO();
                    System.out.println("Processando linha " + (i + 1)); // Exibe o número da linha

                    for (int j = 0; j < headerRow.getPhysicalNumberOfCells(); j++) {
                        String columnHeader = headerRow.getCell(j).getStringCellValue().trim().toLowerCase();
                        String valor = getCellValueAsString(row.getCell(j)).trim();


                        // Comparando os cabeçalhos e atribuindo o valor à coluna correta do DTO
                        switch (columnHeader) {
                            case "nome_delegacia" -> dto.setNome_delegacia(valor);
                            case "data_ocorrencia_bo" -> dto.setData_ocorrncia_bo(parseDate(valor));
                            case "hora_ocorrencia" -> dto.setHora_ocorrncia(parseTime(valor));
                            case "rubrica" -> dto.setRubrica(valor);
                            case "cidade" -> dto.setCidade(valor);
                            case "bairro" -> dto.setBairro(valor);
                            case "cep" -> dto.setCep(valor);
                            case "logradouro" -> dto.setLogradouro(valor);
                            case "descr_tipo_veiculo" -> dto.setDescr_tipo_veiculo(valor);
                            case "descr_marca_veiculo" -> dto.setDescr_marca_veiculo(valor);
                            case "ano_modelo" -> dto.setAno_modelo(valor);
                            case "placa_veiculo" -> dto.setPlaca_veiculo(valor);
                            case "desc_cor_veiculo" -> dto.setDesc_cor_veiculo(valor);
                            case "descr_ocorrencia_veiculo" -> dto.setDescr_ocorrencia_veiculo(valor);
                        }
                    }

                    arquivos.add(dto);
                    var id = obterOuCriarArtigoPorRubrica(dto.getRubrica());
                    DenunciaRequest denunciaRequest = new DenunciaRequest();
                    denunciaRequest.setIdUsuario(1L);
                    denunciaRequest.setCep(dto.getCep());
                    denunciaRequest.setBairro(dto.getBairro());
                    denunciaRequest.setAno(parseInteger(dto.getAno_modelo()));
                    denunciaRequest.setCidade(dto.getCidade());
                    denunciaRequest.setLogradouro(dto.getLogradouro());
                    denunciaRequest.setMarca(dto.getDescr_tipo_veiculo());
                    denunciaRequest.setTipo(dto.getDescr_tipo_veiculo());
                    denunciaRequest.setPlaca(dto.getPlaca_veiculo());
                    denunciaRequest.setEstado("São Paulo");
                    denunciaRequest.setStatusDenuncia("Em andamento");
                    denunciaRequest.setHoraOcorrencia(
                            dto.getHora_ocorrncia() != null ? dto.getHora_ocorrncia().toString() : ""
                    );
                    denunciaRequest.setDescricao(dto.getDescr_ocorrencia_veiculo());
                    if (dto.getData_ocorrncia_bo() != null && dto.getHora_ocorrncia() != null) {
                        denunciaRequest.setDataHora(LocalDateTime.of(dto.getData_ocorrncia_bo(), dto.getHora_ocorrncia()));
                    } else {
                        denunciaRequest.setDataHora(LocalDateTime.now()); // ou null, ou outro default
                    }
                    denunciaRequest.setCor(dto.getDesc_cor_veiculo());
                    denunciaRequest.setArtigoLei(String.valueOf(id));
                    denunciaRequest.setReceberAlertas(true);
                    try {
                        ocorrenciaService.criarDenuncia(denunciaRequest);
                    } catch (Exception e) {
                        System.err.println("Erro ao criar denúncia: " + e.getMessage());
                        throw new RuntimeException(e.getMessage());
                    }
                }

                return "Importação realizada com sucesso!";
            }

        } catch (Exception e) {
            System.out.println("Erro ao importar: " + e.getMessage());
            return "Erro ao importar: " + e.getMessage();
        }
    }

    private String sanitizeString(String value) {
        if (value == null || value.trim().equalsIgnoreCase("NULL")) return "";
        return value.trim();
    }


    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isEmpty() || value.equalsIgnoreCase("NULL")) return null;
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao parsear número: " + value);
            return null; // ou 0, dependendo do seu caso
        }
    }


    private Long obterOuCriarArtigoPorRubrica(String rubrica) {
        if (rubrica == null || rubrica.trim().isEmpty()) {
            return null;
        }

        // Tenta encontrar por código ou descrição
        List<Artigo> encontrados = artigoRepository.findByRubricaInCodigoOuDescricao(rubrica);

        if (!encontrados.isEmpty()) {
            // Retorna o ID do primeiro encontrado
            return encontrados.get(0).getId();
        } else {
            // Cria novo artigo com rubrica como descrição
            Artigo novo = new Artigo();
            novo.setCodArtigo(rubrica); // ou algum valor padrão, se necessário
            novo.setDescricao(rubrica);
            Artigo salvo = artigoRepository.save(novo);
            return salvo.getId();
        }
    }
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return ""; // Ou outro valor padrão
        }

        String value;
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Verificar se o formato da célula é de hora (com segundos)
                    if (cell.getCellStyle().getDataFormatString().contains("h")) {
                        // Formato de hora com segundos (HH:mm:ss)
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        value = timeFormatter.format(cell.getLocalDateTimeCellValue().toLocalTime());
                    } else {
                        // Formato de data (yyyy-MM-dd)
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        value = dateFormatter.format(cell.getLocalDateTimeCellValue().toLocalDate());
                    }
                } else {
                    value = String.valueOf((long) cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                value = getCellValueAsString(cell); // Recursão para fórmulas
                break;
            case BLANK:
                value = "";
                break;
            default:
                value = "";
        }

        // Se o valor for "NULL" (string literal), retorne uma string vazia ou um valor padrão
        if ("NULL".equalsIgnoreCase(value)) {
            value = "";  // Ou outro valor padrão
        }

        return value;
    }



    private LocalDate parseDate(String value) {
        if (value == null || value.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(value, formatter);
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isEmpty()) return null;

        try {
            // Alterar o formato para considerar horas, minutos e segundos
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return LocalTime.parse(value, formatter);
        } catch (Exception e) {
            System.out.println("Erro ao parsear hora: " + value);
            return null; // Ou você pode lançar uma exceção caso precise
        }
    }


    private String getColumnName(int index) {
        return switch (index) {
            case 0 -> "nome_delegacia";
            case 1 -> "data_ocorrncia_bo";
            case 2 -> "hora_ocorrncia";
            case 3 -> "rubrica";
            case 4 -> "cidade";
            case 5 -> "bairro";
            case 6 -> "cep";
            case 7 -> "logradouro";
            case 8 -> "descr_tipo_veiculo";
            case 9 -> "descr_marca_veiculo";
            case 10 -> "ano_modelo";
            case 11 -> "placa_veiculo";
            case 12 -> "desc_cor_veiculo";
            default -> "";
        };
    }
}
