package com.system.watchCar.service;

import com.system.watchCar.dto.OcorrenciaDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    public List<OcorrenciaDTO> consumirCsvPorUrl(String url) {
        List<OcorrenciaDTO> ocorrencias = new ArrayList<>();
        int maxRows = 5;

        try (InputStream inputStream = new URL(url).openStream()) {


            IOUtils.setByteArrayMaxOverride(600000000);

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Lê até 5 linhas
            for (int rowNum = 0; rowNum < Math.min(maxRows, sheet.getPhysicalNumberOfRows()); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    OcorrenciaDTO ocorrencia = new OcorrenciaDTO();

                // Exemplo, ajustando conforme sua planilha:
                if (row.getCell(0) != null) ocorrencia.setIdDelegacia(row.getCell(0).getStringCellValue()); // ID_DELEGACIA
                if (row.getCell(1) != null) ocorrencia.setNomeDepartamento(row.getCell(1).getStringCellValue()); // NOME_DEPARTAMENTO
                if (row.getCell(2) != null) ocorrencia.setNomeSeccional(row.getCell(2).getStringCellValue()); // NOME_SECCIONAL
                if (row.getCell(3) != null) ocorrencia.setNomeDelegacia(row.getCell(3).getStringCellValue()); // NOME_DELEGACIA
                if (row.getCell(4) != null) ocorrencia.setNomeMunicipio(row.getCell(4).getStringCellValue()); // NOME_MUNICIPIO
                if (row.getCell(5) != null) ocorrencia.setAnoBo(row.getCell(5).getStringCellValue()); // ANO_BO
                if (row.getCell(6) != null) ocorrencia.setNumBo(row.getCell(6).getStringCellValue()); // NUM_BO
                if (row.getCell(7) != null) ocorrencia.setVersao(row.getCell(7).getStringCellValue()); // VERSAO
                if (row.getCell(8) != null) ocorrencia.setCidade(row.getCell(8).getStringCellValue()); // CIDADE
                if (row.getCell(9) != null) ocorrencia.setNomeDepartamentoCirc(row.getCell(9).getStringCellValue()); // NOME_DEPARTAMENTO_CIRC
                if (row.getCell(10) != null) ocorrencia.setNomeSeccionalCirc(row.getCell(10).getStringCellValue()); // NOME_SECCIONAL_CIRC
                if (row.getCell(11) != null) ocorrencia.setNomeDelegaciaCirc(row.getCell(11).getStringCellValue()); // NOME_DELEGACIA_CIRC
                if (row.getCell(12) != null) ocorrencia.setNomeMunicipioCirc(row.getCell(12).getStringCellValue()); // NOME_MUNICIPIO_CIRC
                if (row.getCell(13) != null) ocorrencia.setDataOcorrenciaBo(row.getCell(13).getStringCellValue()); // DATA_OCORRENCIA_BO
                if (row.getCell(14) != null) ocorrencia.setHoraOcorrencia(row.getCell(14).getStringCellValue()); // HORA_OCORRENCIA
                if (row.getCell(15) != null) ocorrencia.setDescricaoApresentacao(row.getCell(15).getStringCellValue()); // DESCRICAO_APRESENTACAO
                if (row.getCell(16) != null) ocorrencia.setDatahoraRegistroBo(row.getCell(16).getStringCellValue()); // DATAHORA_REGISTRO_BO
                if (row.getCell(17) != null) ocorrencia.setDataComunicacaoBo(row.getCell(17).getStringCellValue()); // DATA_COMUNICACAO_BO
                if (row.getCell(18) != null) ocorrencia.setDatahoraImpressaoBo(row.getCell(18).getStringCellValue()); // DATAHORA_IMPRESSAO_BO
                if (row.getCell(19) != null) ocorrencia.setDescrPeriodo(row.getCell(19).getStringCellValue()); // DESCR_PERIODO
                if (row.getCell(20) != null) ocorrencia.setAutoriaBo(row.getCell(20).getStringCellValue()); // AUTORIA_BO
                if (row.getCell(21) != null) ocorrencia.setFlagIntolerancia(row.getCell(21).getStringCellValue()); // FLAG_INTOLERANCIA
                if (row.getCell(22) != null) ocorrencia.setTipoIntolerancia(row.getCell(22).getStringCellValue()); // TIPO_INTOLERANCIA
                if (row.getCell(23) != null) ocorrencia.setFlagFlagrante(row.getCell(23).getStringCellValue()); // FLAG_FLAGRANTE
                if (row.getCell(24) != null) ocorrencia.setFlagStatus(row.getCell(24).getStringCellValue()); // FLAG_STATUS
                if (row.getCell(25) != null) ocorrencia.setDescLei(row.getCell(25).getStringCellValue()); // DESC_LEI
                if (row.getCell(26) != null) ocorrencia.setFlagAtoInfracional(row.getCell(26).getStringCellValue()); // FLAG_ATO_INFRACIONAL
                if (row.getCell(27) != null) ocorrencia.setRubrica(row.getCell(27).getStringCellValue()); // RUBRICA
                if (row.getCell(28) != null) ocorrencia.setDescrConduta(row.getCell(28).getStringCellValue()); // DESCR_CONDUTA
                if (row.getCell(29) != null) ocorrencia.setDesdobramento(row.getCell(29).getStringCellValue()); // DESDOBRAMENTO
                if (row.getCell(30) != null) ocorrencia.setCircunstancia(row.getCell(30).getStringCellValue()); // CIRCUNSTANCIA
                if (row.getCell(31) != null) ocorrencia.setDescrTipoLocal(row.getCell(31).getStringCellValue()); // DESCR_TIPOLOCAL
                if (row.getCell(32) != null) ocorrencia.setDescrSubtipoLocal(row.getCell(32).getStringCellValue()); // DESCR_SUBTIPOLOCAL
                if (row.getCell(33) != null) ocorrencia.setCidadeLocal(row.getCell(33).getStringCellValue()); // CIDADE
                if (row.getCell(34) != null) ocorrencia.setBairro(row.getCell(34).getStringCellValue()); // BAIRRO
                if (row.getCell(35) != null) ocorrencia.setCep(row.getCell(35).getStringCellValue()); // CEP
                if (row.getCell(36) != null) ocorrencia.setDescNaturezaLocal(row.getCell(36).getStringCellValue()); // DESC_NATUREZA_LOCAL
                if (row.getCell(37) != null) ocorrencia.setLogradouroVersao(row.getCell(37).getStringCellValue()); // LOGRADOURO_VERSAO
                if (row.getCell(38) != null) ocorrencia.setLogradouro(row.getCell(38).getStringCellValue()); // LOGRADOURO
                if (row.getCell(39) != null) ocorrencia.setNumeroLogradouro(row.getCell(39).getStringCellValue()); // NUMERO_LOGRADOURO
                if (row.getCell(40) != null) ocorrencia.setLatitude(row.getCell(40).getStringCellValue()); // LATITUDE
                if (row.getCell(41) != null) ocorrencia.setLongitude(row.getCell(41).getStringCellValue()); // LONGITUDE
                if (row.getCell(42) != null) ocorrencia.setContVeiculo(row.getCell(42).getStringCellValue()); // CONT_VEICULO
                if (row.getCell(43) != null) ocorrencia.setDescrOcorrenciaVeiculo(row.getCell(43).getStringCellValue()); // DESCR_OCORRENCIA_VEICULO
                if (row.getCell(44) != null) ocorrencia.setDescrTipoVeiculo(row.getCell(44).getStringCellValue()); // DESCR_TIPO_VEICULO
                if (row.getCell(45) != null) ocorrencia.setDescrMarcaVeiculo(row.getCell(45).getStringCellValue()); // DESCR_MARCA_VEICULO
                if (row.getCell(46) != null) ocorrencia.setAnoFabricacao(row.getCell(46).getStringCellValue()); // ANO_FABRICACAO
                if (row.getCell(47) != null) ocorrencia.setAnoModelo(row.getCell(47).getStringCellValue()); // ANO_MODELO
                if (row.getCell(48) != null) ocorrencia.setPlacaVeiculo(row.getCell(48).getStringCellValue()); // PLACA_VEICULO
                if (row.getCell(49) != null) ocorrencia.setDescCorVeiculo(row.getCell(49).getStringCellValue()); // DESC_COR_VEICULO
                if (row.getCell(50) != null) ocorrencia.setMes(row.getCell(50).getStringCellValue()); // MES
                if (row.getCell(51) != null) ocorrencia.setAno(row.getCell(51).getStringCellValue()); // ANO

                    ocorrencias.add(ocorrencia);
                }
            }

            // Fechar o workbook para liberar memória
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ocorrencias;
    }
}