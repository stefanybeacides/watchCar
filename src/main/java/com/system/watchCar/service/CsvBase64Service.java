package com.system.watchCar.service;

import com.system.watchCar.dto.OcorrenciaDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

@Service
public class CsvBase64Service {

    public List<OcorrenciaDTO> consumirCsvBase64(String base64Data) throws Exception {
        // Decodificar o arquivo CSV de base64 para binário
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);
        Reader reader = new InputStreamReader(byteArrayInputStream);

        // Usando a biblioteca Apache Commons CSV para fazer o parse do CSV
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        List<OcorrenciaDTO> ocorrencias = new ArrayList<>();
        for (CSVRecord record : csvParser) {
            OcorrenciaDTO ocorrencia = new OcorrenciaDTO();

            // Setando os campos do CSV
            ocorrencia.setIdDelegacia(record.get("ID_DELEGACIA"));
            ocorrencia.setNomeDepartamento(record.get("NOME_DEPARTAMENTO"));
            ocorrencia.setNomeSeccional(record.get("NOME_SECCIONAL"));
            ocorrencia.setNomeDelegacia(record.get("NOME_DELEGACIA"));
            ocorrencia.setNomeMunicipio(record.get("NOME_MUNICIPIO"));
            ocorrencia.setAnoBo(record.get("ANO_BO"));
            ocorrencia.setNumBo(record.get("NUM_BO"));
            ocorrencia.setVersao(record.get("VERSAO"));
            ocorrencia.setCidade(record.get("CIDADE"));
            ocorrencia.setNomeDepartamentoCirc(record.get("NOME_DEPARTAMENTO_CIRC"));
            ocorrencia.setNomeSeccionalCirc(record.get("NOME_SECCIONAL_CIRC"));
            ocorrencia.setNomeDelegaciaCirc(record.get("NOME_DELEGACIA_CIRC"));
            ocorrencia.setNomeMunicipioCirc(record.get("NOME_MUNICIPIO_CIRC"));
            ocorrencia.setDataOcorrenciaBo(record.get("DATA_OCORRENCIA_BO"));
            ocorrencia.setHoraOcorrencia(record.get("HORA_OCORRENCIA"));
            ocorrencia.setDescricaoApresentacao(record.get("DESCRICAO_APRESENTACAO"));
            ocorrencia.setDatahoraRegistroBo(record.get("DATAHORA_REGISTRO_BO"));
            ocorrencia.setDataComunicacaoBo(record.get("DATA_COMUNICACAO_BO"));
            ocorrencia.setDatahoraImpressaoBo(record.get("DATAHORA_IMPRESSAO_BO"));
            ocorrencia.setDescrPeriodo(record.get("DESCR_PERIODO"));
            ocorrencia.setAutoriaBo(record.get("AUTORIA_BO"));
            ocorrencia.setFlagIntolerancia(record.get("FLAG_INTOLERANCIA"));
            ocorrencia.setTipoIntolerancia(record.get("TIPO_INTOLERANCIA"));
            ocorrencia.setFlagFlagrante(record.get("FLAG_FLAGRANTE"));
            ocorrencia.setFlagStatus(record.get("FLAG_STATUS"));
            ocorrencia.setDescLei(record.get("DESC_LEI"));
            ocorrencia.setFlagAtoInfracional(record.get("FLAG_ATO_INFRACIONAL"));
            ocorrencia.setRubrica(record.get("RUBRICA"));
            ocorrencia.setDescrConduta(record.get("DESCR_CONDUTA"));
            ocorrencia.setDesdobramento(record.get("DESDOBRAMENTO"));
            ocorrencia.setCircunstancia(record.get("CIRCUNSTANCIA"));
            ocorrencia.setDescrTipoLocal(record.get("DESCR_TIPOLOCAL"));
            ocorrencia.setDescrSubtipoLocal(record.get("DESCR_SUBTIPOLOCAL"));
            ocorrencia.setCidadeLocal(record.get("CIDADE"));
            ocorrencia.setBairro(record.get("BAIRRO"));
            ocorrencia.setCep(record.get("CEP"));
            ocorrencia.setDescNaturezaLocal(record.get("DESC_NATUREZA_LOCAL"));
            ocorrencia.setLogradouroVersao(record.get("LOGRADOURO_VERSAO"));
            ocorrencia.setLogradouro(record.get("LOGRADOURO"));
            ocorrencia.setNumeroLogradouro(record.get("NUMERO_LOGRADOURO"));
            ocorrencia.setLatitude(record.get("LATITUDE"));
            ocorrencia.setLongitude(record.get("LONGITUDE"));
            ocorrencia.setContVeiculo(record.get("CONT_VEICULO"));
            ocorrencia.setDescrOcorrenciaVeiculo(record.get("DESCR_OCORRENCIA_VEICULO"));
            ocorrencia.setDescrTipoVeiculo(record.get("DESCR_TIPO_VEICULO"));
            ocorrencia.setDescrMarcaVeiculo(record.get("DESCR_MARCA_VEICULO"));
            ocorrencia.setAnoFabricacao(record.get("ANO_FABRICACAO"));
            ocorrencia.setAnoModelo(record.get("ANO_MODELO"));
            ocorrencia.setPlacaVeiculo(record.get("PLACA_VEICULO"));
            ocorrencia.setDescCorVeiculo(record.get("DESC_COR_VEICULO"));
            ocorrencia.setMes(record.get("MES"));
            ocorrencia.setAno(record.get("ANO"));

            ocorrencias.add(ocorrencia);
        }
        return ocorrencias;
    }
}


