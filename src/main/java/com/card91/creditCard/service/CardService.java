package com.card91.creditCard.service;

import com.card91.creditCard.dao.CardRepository;
import com.card91.creditCard.model.Card;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    private String serviceCode = "620";

    public Card saveCard(Card card) {
        card.setValidityDate(getExpiryDate(0));
        card.setExpiryDate(getExpiryDate(Integer.parseInt(card.getExpiryYears())));
        card.setCardNo((card.getBin() + getRandomDigit(Integer.parseInt(card.getCardLength()) - card.getBin().length())));
        card.setCvv(getRandomDigit(3));
        card.setCvv2(getRandomDigit(3));
        card.setIcvd(getRandomDigit(3));
        card.setPackId(getRandomDigit(12));
        card.setHash(convertHashNum(card.getCardNo()));
        card.setName1(card.getName1().trim());
        card.setDescritionaryData(getDescritionaryData(card.getCvv()));
        return cardRepository.save(card);
    }

    private String getDescritionaryData(String cvv) {
        int random = (int) (Math.random() * 1000000);
        return "1" + random + cvv;
    }

    public static String formatCardNum(String cardNumber) {
        return cardNumber == null ? null : cardNumber.replaceAll(".{4}(?!$)", "$0" + ' ');
    }

    public String getExpiryDate(Integer year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 12*year);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
        return dateFormat.format(calendar.getTime());
    }

    public String getRandomDigit(int length) {
        int temp = (int) Math.pow(10, length - 1);
        String randomNum = String.valueOf(Math.abs(temp + new Random().nextInt(9 * temp)));
        for(int i= 0; randomNum.length() < length && length > 8; i++ ) {
            randomNum += Math.abs(new Random().nextInt(1,11));
        }
        return randomNum;
    }

    public String convertHashNum(String cardNum) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(cardNum.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Card> getAllCards() {
       return cardRepository.findAll();
    }

    public List<Card> getAllCards(String refId) {
        return cardRepository.findAllByRefId(refId);
    }

    public String checkForDuplicate(Card card) {
        String duplicate = "";
        if(! (cardRepository.findAllByRefId(card.getRefId()).isEmpty())) duplicate += " Reference Id";
        return duplicate;
    }

    @Transactional
    public void getAllCardsPrint() throws IOException {
        boolean append = true;
        String basePath = "/home/piyushjunonkar/card91/files/";
        String fileName = basePath + getFileNameWithDateTime();
//        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, append));
        List<Card> cards = cardRepository.findAll();

//        List<String> previousData = readPreviousData(basePath);
        String texts = "" ;
        for (Card card : cards) {
            if(!(cardRepository.findById(card.getCardId()).get().getPrinted())) {
                StringBuilder text =
                        new StringBuilder(String.format("%-6s", card.getCardId()));
                text.append(String.format("%-27s", "#") + "$");
                text.append(formatCardNum(card.getCardNo()));
                text.append(String.format("%-12s", "#"));
                text.append(getNormalDate(card.getExpiryDate()));
                text.append(getNormalDate(card.getValidityDate()) + "   *");
                text.append(String.format("%-26s", card.getName1()) + ">");
                text.append(String.format("%-26s", card.getName2()) + "\"");
                text.append(card.getCardNo().substring(card.getCardNo().length() - 4));
                text.append(" " + card.getCvv2() + String.format("%-12s", "@") + "%B" + card.getCardNo() + "^");
                text.append(String.format("%-25s", card.getName1()) + "/^" + formatDate(card.getExpiryDate()));
                text.append(serviceCode);
                text.append(card.getCvv() + card.getAdhaarNum());
                text.append(card.getDescritionaryData().substring(0, 7) + "? ;" + card.getCardNo() + "=");
                text.append(formatDate(card.getExpiryDate()));
                text.append(serviceCode + card.getCvv());
                text.append(card.getDescritionaryData() + "? =");
                text.append(String.format("%-22s", card.getCardNo()) + "=");
                text.append(String.format("%-47s", card.getName1()) + "=");
                text.append(String.format("%-46s", card.getAddress1()) + "=");
                text.append(String.format("%-46s", card.getAddress2()) + "=");
                text.append(String.format("%-46s", card.getAddress3()) + "=");
                text.append(String.format("%-46s", card.getState()) + "=");
                text.append(String.format("%-41s", card.getCityZip()) + "=");
                text.append(String.format("%-31s", card.getCountry()) + "=");
                text.append(String.format("%-16s", card.getPhoneNum()) + " ");
                text.append(card.getPackId() + " " + card.getIcvd() + "\n");
                texts += text;
                cardRepository.updateIsPrinted(card.getRefId(), true);
            }
        }
        if(!(texts.isEmpty()))        encryptPrintFile(texts);
    }

//    private boolean isDataDuplicate(String data, List<String> previousData) {
//        for (String previousDatum : previousData) {
//            if (previousDatum.equals(data)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void saveCurrentDataAsPreviousData(String fileName, String folderPath) throws IOException {
//        Path folder = Path.of(folderPath);
//        if (Files.notExists(folder)) {
//            Files.createDirectories(folder);
//        }
//        Files.copy(Path.of(fileName), folder.resolve(Path.of(fileName).getFileName()));
//    }

    private String getFileNameWithDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmm");
        Date date = new Date();
        return "original_" + dateFormat.format(date) + ".txt";
    }

//    private List<String> readPreviousData(String folderPath) throws IOException {
//        List<String> previousData = new ArrayList<>();
//        Path folder = Path.of(folderPath);
//        if (Files.exists(folder) && Files.isDirectory(folder)) {
//            Files.walk(folder)
//                    .filter(Files::isRegularFile)
//                    .forEach(file -> {
//                        try {
//                            previousData.addAll(Files.readAllLines(file));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//        }
//        return previousData;
//    }

//    @Value("/home/piyushjunonkar/card91/keys/pub_key/piyush_pub_key.pgp")
//    private Resource publicKeyResource;

    @Value("/home/piyushjunonkar/card91/keys/pub_key/piyush_pub_key.pgp")
    private String publicKeyResource;
    @Value("/home/piyushjunonkar/card91/keys/prv_key/piyush_prv_key.pgp")
    private Resource privateKeyResource;

//    private void encryptPrintFile(String fileName) {
//        File file = new File(fileName);
//        if (file.length() == 0) {
//            file.delete();
//        } else {
//            try {
//                String outputFolderPath = "/home/piyushjunonkar/card91/output";
//                String publicKeyFileName = publicKeyResource;
//                String encryptedFileName = getEncryptedFileName(fileName);
//                String encryptedFilePath = outputFolderPath + "/" + encryptedFileName;
//
//                KeyBasedFileProcessor.encryptFile(encryptedFilePath, fileName, "/home/piyushjunonkar/card91/keys/pub_key/piyush_pub_key.pgp", true, true);
//                decryptPrintFile(encryptedFilePath,encryptedFileName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void encryptPrintFile(String stream) {
            try {
                String outputFolderPath = "/home/piyushjunonkar/card91/output";
                String publicKeyFileName = publicKeyResource;
                String encryptedFileName = getEncryptedFileName();
                String encryptedFilePath = outputFolderPath + "/" + encryptedFileName;

                KeyBasedFileProcessor.encryptFile(encryptedFilePath, stream, "/home/piyushjunonkar/card91/keys/pub_key/piyush_pub_key.pgp", true, true);
//                decryptPrintFile(encryptedFilePath,encryptedFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void decryptPrintFile(String fileNameWithPath, String fileName) {
        try {
            String privateKeyFileName = "/home/piyushjunonkar/card91/keys/prv_key/piyush_prv_key.pgp";
            KeyBasedFileProcessor.decryptFile(fileNameWithPath,privateKeyFileName,"Rupiyu@12345".toCharArray(), getDecryptedFileName(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDecryptedFileName(String fileName) {
        String outputFolderPath = "/home/piyushjunonkar/card91/decryptFiles/";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd_HHmm");
        String timestamp = dateFormat.format(new Date());
        return outputFolderPath + "decrypted" + "_"+ fileName + "_" + timestamp + ".txt";
    }
    private String getEncryptedFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd_HHmm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        String encryptedFileName = "encrypted" + "_" + timestamp + ".pgp";
        return encryptedFileName;
    }

    private String getNormalDate(String date) {
        String[] dates = date.split("/");
        return dates[0] + dates[1];
    }

    public String formatDate(String date) {
        String[] dates = date.split("/");
        Collections.reverse(Arrays.asList(dates));
        return dates[0] + dates[1];
    }
}
