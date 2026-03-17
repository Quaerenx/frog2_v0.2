package com.company.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.company.util.DBConnection;

public class CustomerDetailDAO {

    // ê³ ê°???ì„¸?•ë³´ ì¡°íšŒ
    public CustomerDetailDTO getCustomerDetail(String customerName) {
        CustomerDetailDTO detail = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM vertica_customer_detail WHERE customer_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                detail = mapRowToDetail(rs);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return detail;
    }

    // ?¤í…Œ?´ì§• ?ì„¸?•ë³´ ì¡°íšŒ
    public CustomerDetailDTO getCustomerDetailStg(String customerName) {
        return getCustomerDetailFromTable(customerName, "vertica_customer_detail_stg");
    }

    // ê°œë°œ ?ì„¸?•ë³´ ì¡°íšŒ
    public CustomerDetailDTO getCustomerDetailDev(String customerName) {
        return getCustomerDetailFromTable(customerName, "vertica_customer_detail_dev");
    }

    // ê³µí†µ: ?¹ì • ?Œì´ë¸”ì—???ì„¸?•ë³´ ì¡°íšŒ
    private CustomerDetailDTO getCustomerDetailFromTable(String customerName, String tableName) {
        CustomerDetailDTO detail = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM " + tableName + " WHERE customer_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                detail = mapRowToDetail(rs);
            }
        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(rs, pstmt, conn);
        }

        return detail;
    }

    // ê³µí†µ: ResultSet -> DTO ë§¤í•‘
    private CustomerDetailDTO mapRowToDetail(ResultSet rs) throws SQLException {
        CustomerDetailDTO detail = new CustomerDetailDTO();
        detail.setCustomerName(rs.getString("customer_name"));
        detail.setSystemName(rs.getString("system_name"));
        detail.setCustomerManager(rs.getString("customer_manager"));
        detail.setSiCompany(rs.getString("si_company"));
        detail.setSiManager(rs.getString("si_manager"));
        detail.setCreator(rs.getString("creator"));

        Timestamp createTs = rs.getTimestamp("create_date");
        if (createTs != null) {
            detail.setCreateDate(new java.util.Date(createTs.getTime()));
        }

        detail.setMainManager(rs.getString("main_manager"));
        detail.setSubManager(rs.getString("sub_manager"));

        Timestamp installTs = rs.getTimestamp("install_date");
        if (installTs != null) {
            detail.setInstallDate(new java.util.Date(installTs.getTime()));
        }

        detail.setIntroductionYear(rs.getString("introduction_year"));

        // Vertica ?•ë³´
        detail.setDbName(rs.getString("db_name"));
        detail.setDbMode(rs.getString("db_mode"));
        detail.setVerticaVersion(rs.getString("vertica_version"));
        detail.setLicenseInfo(rs.getString("license_info"));
        detail.setSaid(rs.getString("said"));
        detail.setNodeCount(rs.getString("node_count"));
        detail.setVerticaAdmin(rs.getString("vertica_admin"));
        detail.setSubclusterYn(rs.getString("subcluster_yn"));
        detail.setMcYn(rs.getString("mc_yn"));
        detail.setMcHost(rs.getString("mc_host"));
        detail.setMcVersion(rs.getString("mc_version"));
        detail.setMcAdmin(rs.getString("mc_admin"));
        detail.setBackupYn(rs.getString("backup_yn"));
        detail.setCustomResourcePoolYn(rs.getString("custom_resource_pool_yn"));
        detail.setBackupNote(rs.getString("backup_note"));

        // ?˜ê²½ ?•ë³´
        detail.setOsInfo(rs.getString("os_info"));
        detail.setMemoryInfo(rs.getString("memory_info"));
        detail.setInfraType(rs.getString("infra_type"));
        detail.setCpuSocket(rs.getString("cpu_socket"));
        detail.setHyperThreading(rs.getString("hyper_threading"));
        detail.setCpuCore(rs.getString("cpu_core"));
        detail.setDataArea(rs.getString("data_area"));
        detail.setDepotArea(rs.getString("depot_area"));
        detail.setCatalogArea(rs.getString("catalog_area"));
        detail.setObjectArea(rs.getString("object_area"));
        detail.setPublicYn(rs.getString("public_yn"));
        detail.setPublicNetwork(rs.getString("public_network"));
        detail.setPrivateYn(rs.getString("private_yn"));
        detail.setPrivateNetwork(rs.getString("private_network"));
        detail.setStorageYn(rs.getString("storage_yn"));
        detail.setStorageNetwork(rs.getString("storage_network"));

        // ?¸ë? ?”ë£¨??        detail.setEtlTool(rs.getString("etl_tool"));
        detail.setBiTool(rs.getString("bi_tool"));
        detail.setDbEncryption(rs.getString("db_encryption"));
        detail.setCdcTool(rs.getString("cdc_tool"));

        // ê¸°í?
        Timestamp eosTs = rs.getTimestamp("eos_date");
        if (eosTs != null) {
            detail.setEosDate(new java.util.Date(eosTs.getTime()));
        }

        detail.setCustomerType(rs.getString("customer_type"));
        detail.setNote(rs.getString("note"));
        return detail;
    }

    // ê³ ê°???ì„¸?•ë³´ ?€???ëŠ” ?…ë°?´íŠ¸
    public boolean saveOrUpdateCustomerDetail(CustomerDetailDTO detail) {
        // ë¨¼ì? ê¸°ì¡´ ?°ì´?°ê? ?ˆëŠ”ì§€ ?•ì¸
        CustomerDetailDTO existing = getCustomerDetail(detail.getCustomerName());

        if (existing != null) {
            return updateCustomerDetail(detail);
        } else {
            return insertCustomerDetail(detail);
        }
    }

    // ?¤í…Œ?´ì§• ?€???ëŠ” ?…ë°?´íŠ¸
    public boolean saveOrUpdateCustomerDetailStg(CustomerDetailDTO detail) {
        CustomerDetailDTO existing = getCustomerDetailFromTable(detail.getCustomerName(), "vertica_customer_detail_stg");
        if (existing != null) {
            return updateCustomerDetailByTable(detail, "vertica_customer_detail_stg");
        } else {
            return insertCustomerDetailByTable(detail, "vertica_customer_detail_stg");
        }
    }

    // ê°œë°œ ?€???ëŠ” ?…ë°?´íŠ¸
    public boolean saveOrUpdateCustomerDetailDev(CustomerDetailDTO detail) {
        CustomerDetailDTO existing = getCustomerDetailFromTable(detail.getCustomerName(), "vertica_customer_detail_dev");
        if (existing != null) {
            return updateCustomerDetailByTable(detail, "vertica_customer_detail_dev");
        } else {
            return insertCustomerDetailByTable(detail, "vertica_customer_detail_dev");
        }
    }

    // ê³ ê°???ì„¸?•ë³´ ì¶”ê?
    private boolean insertCustomerDetail(CustomerDetailDTO detail) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO vertica_customer_detail (" +
                    "customer_name, system_name, customer_manager, si_company, si_manager, creator, create_date, " +
                    "main_manager, sub_manager, install_date, introduction_year, db_name, db_mode, " +
                    "vertica_version, license_info, said, node_count, vertica_admin, subcluster_yn, " +
                    "mc_yn, mc_host, mc_version, mc_admin, backup_yn, custom_resource_pool_yn, " +
                    "backup_note, os_info, memory_info, infra_type, cpu_socket, hyper_threading, " +
                    "cpu_core, data_area, depot_area, catalog_area, object_area, public_yn, " +
                    "public_network, private_yn, private_network, storage_yn, storage_network, " +
                    "etl_tool, bi_tool, db_encryption, cdc_tool, eos_date, customer_type, note" +
                    ") VALUES (" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                    ")";

            pstmt = conn.prepareStatement(sql);
            setDetailParameters(pstmt, detail);

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ê³µí†µ: ?¹ì • ?Œì´ë¸”ì— ?ì„¸?•ë³´ ì¶”ê?
    private boolean insertCustomerDetailByTable(CustomerDetailDTO detail, String tableName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = ("INSERT INTO " + tableName + " (" +
                    "customer_name, system_name, customer_manager, si_company, si_manager, creator, create_date, " +
                    "main_manager, sub_manager, install_date, introduction_year, db_name, db_mode, " +
                    "vertica_version, license_info, said, node_count, vertica_admin, subcluster_yn, " +
                    "mc_yn, mc_host, mc_version, mc_admin, backup_yn, custom_resource_pool_yn, " +
                    "backup_note, os_info, memory_info, infra_type, cpu_socket, hyper_threading, " +
                    "cpu_core, data_area, depot_area, catalog_area, object_area, public_yn, " +
                    "public_network, private_yn, private_network, storage_yn, storage_network, " +
                    "etl_tool, bi_tool, db_encryption, cdc_tool, eos_date, customer_type, note" +
                    ") VALUES (" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                    ")");

            pstmt = conn.prepareStatement(sql);
            setDetailParameters(pstmt, detail);

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ê³ ê°???ì„¸?•ë³´ ?…ë°?´íŠ¸
    private boolean updateCustomerDetail(CustomerDetailDTO detail) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE vertica_customer_detail SET " +
                    "system_name = ?, customer_manager = ?, si_company = ?, si_manager = ?, creator = ?, create_date = ?, " +
                    "main_manager = ?, sub_manager = ?, install_date = ?, introduction_year = ?, " +
                    "db_name = ?, db_mode = ?, vertica_version = ?, license_info = ?, said = ?, " +
                    "node_count = ?, vertica_admin = ?, subcluster_yn = ?, mc_yn = ?, mc_host = ?, " +
                    "mc_version = ?, mc_admin = ?, backup_yn = ?, custom_resource_pool_yn = ?, " +
                    "backup_note = ?, os_info = ?, memory_info = ?, infra_type = ?, cpu_socket = ?, " +
                    "hyper_threading = ?, cpu_core = ?, data_area = ?, depot_area = ?, catalog_area = ?, " +
                    "object_area = ?, public_yn = ?, public_network = ?, private_yn = ?, private_network = ?, " +
                    "storage_yn = ?, storage_network = ?, etl_tool = ?, bi_tool = ?, db_encryption = ?, " +
                    "cdc_tool = ?, eos_date = ?, customer_type = ?, note = ? " +
                    "WHERE customer_name = ?";

            pstmt = conn.prepareStatement(sql);

            // UPDATE ?„ìš© ?Œë¼ë¯¸í„° ?¤ì • (customer_name ?œì™¸)
         // UPDATE ?„ìš© ?Œë¼ë¯¸í„° ?¤ì • (customer_name ?œì™¸)
            pstmt.setString(1, detail.getSystemName());           // system_name (ì¶”ê?)
            pstmt.setString(2, detail.getCustomerManager());     // customer_manager
            pstmt.setString(3, detail.getSiCompany());           // si_company
            pstmt.setString(4, detail.getSiManager());           // si_manager
            pstmt.setString(5, detail.getCreator());             // creator

            if (detail.getCreateDate() != null) {
                pstmt.setTimestamp(6, new Timestamp(detail.getCreateDate().getTime()));
            } else {
                pstmt.setTimestamp(6, null);
            }

            pstmt.setString(7, detail.getMainManager());         // main_manager
            pstmt.setString(8, detail.getSubManager());          // sub_manager

            if (detail.getInstallDate() != null) {
                pstmt.setTimestamp(9, new Timestamp(detail.getInstallDate().getTime()));
            } else {
                pstmt.setTimestamp(9, null);
            }

            pstmt.setString(10, detail.getIntroductionYear());    // introduction_year
            pstmt.setString(11, detail.getDbName());             // db_name
            pstmt.setString(12, detail.getDbMode());             // db_mode
            pstmt.setString(13, detail.getVerticaVersion());     // vertica_version
            pstmt.setString(14, detail.getLicenseInfo());        // license_info
            pstmt.setString(15, detail.getSaid());               // said
            pstmt.setString(16, detail.getNodeCount());          // node_count
            pstmt.setString(17, detail.getVerticaAdmin());       // vertica_admin
            pstmt.setString(18, detail.getSubclusterYn());       // subcluster_yn
            pstmt.setString(19, detail.getMcYn());               // mc_yn
            pstmt.setString(20, detail.getMcHost());             // mc_host
            pstmt.setString(21, detail.getMcVersion());          // mc_version
            pstmt.setString(22, detail.getMcAdmin());            // mc_admin
            pstmt.setString(23, detail.getBackupYn());           // backup_yn
            pstmt.setString(24, detail.getCustomResourcePoolYn()); // custom_resource_pool_yn
            pstmt.setString(25, detail.getBackupNote());         // backup_note
            pstmt.setString(26, detail.getOsInfo());             // os_info
            pstmt.setString(27, detail.getMemoryInfo());         // memory_info
            pstmt.setString(28, detail.getInfraType());          // infra_type
            pstmt.setString(29, detail.getCpuSocket());          // cpu_socket
            pstmt.setString(30, detail.getHyperThreading());     // hyper_threading
            pstmt.setString(31, detail.getCpuCore());            // cpu_core
            pstmt.setString(32, detail.getDataArea());           // data_area
            pstmt.setString(33, detail.getDepotArea());          // depot_area
            pstmt.setString(34, detail.getCatalogArea());        // catalog_area
            pstmt.setString(35, detail.getObjectArea());         // object_area
            pstmt.setString(36, detail.getPublicYn());           // public_yn
            pstmt.setString(37, detail.getPublicNetwork());      // public_network
            pstmt.setString(38, detail.getPrivateYn());          // private_yn
            pstmt.setString(39, detail.getPrivateNetwork());     // private_network
            pstmt.setString(40, detail.getStorageYn());          // storage_yn
            pstmt.setString(41, detail.getStorageNetwork());     // storage_network
            pstmt.setString(42, detail.getEtlTool());            // etl_tool
            pstmt.setString(43, detail.getBiTool());             // bi_tool
            pstmt.setString(44, detail.getDbEncryption());       // db_encryption
            pstmt.setString(45, detail.getCdcTool());            // cdc_tool

            if (detail.getEosDate() != null) {
                pstmt.setTimestamp(46, new Timestamp(detail.getEosDate().getTime()));
            } else {
                pstmt.setTimestamp(46, null);
            }

            pstmt.setString(47, detail.getCustomerType());       // customer_type
            pstmt.setString(48, detail.getNote());               // note
            pstmt.setString(49, detail.getCustomerName());       // WHERE ì¡°ê±´
            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

    // ê³µí†µ: ?¹ì • ?Œì´ë¸??…ë°?´íŠ¸
    private boolean updateCustomerDetailByTable(CustomerDetailDTO detail, String tableName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = ("UPDATE " + tableName + " SET " +
                    "system_name = ?, customer_manager = ?, si_company = ?, si_manager = ?, creator = ?, create_date = ?, " +
                    "main_manager = ?, sub_manager = ?, install_date = ?, introduction_year = ?, " +
                    "db_name = ?, db_mode = ?, vertica_version = ?, license_info = ?, said = ?, " +
                    "node_count = ?, vertica_admin = ?, subcluster_yn = ?, mc_yn = ?, mc_host = ?, " +
                    "mc_version = ?, mc_admin = ?, backup_yn = ?, custom_resource_pool_yn = ?, " +
                    "backup_note = ?, os_info = ?, memory_info = ?, infra_type = ?, cpu_socket = ?, " +
                    "hyper_threading = ?, cpu_core = ?, data_area = ?, depot_area = ?, catalog_area = ?, " +
                    "object_area = ?, public_yn = ?, public_network = ?, private_yn = ?, private_network = ?, " +
                    "storage_yn = ?, storage_network = ?, etl_tool = ?, bi_tool = ?, db_encryption = ?, " +
                    "cdc_tool = ?, eos_date = ?, customer_type = ?, note = ? " +
                    "WHERE customer_name = ?");

            pstmt = conn.prepareStatement(sql);

            // ê¸°ì¡´ setDetailParameters ë¡œì§???¬ì‚¬?©í•˜ê¸??´ë µê¸°ì— ?„ëž˜?€ ê°™ì´ ?¤ì •
            pstmt.setString(1, detail.getSystemName());
            pstmt.setString(2, detail.getCustomerManager());
            pstmt.setString(3, detail.getSiCompany());
            pstmt.setString(4, detail.getSiManager());
            pstmt.setString(5, detail.getCreator());
            if (detail.getCreateDate() != null) {
                pstmt.setTimestamp(6, new Timestamp(detail.getCreateDate().getTime()));
            } else {
                pstmt.setTimestamp(6, null);
            }
            pstmt.setString(7, detail.getMainManager());
            pstmt.setString(8, detail.getSubManager());
            if (detail.getInstallDate() != null) {
                pstmt.setTimestamp(9, new Timestamp(detail.getInstallDate().getTime()));
            } else {
                pstmt.setTimestamp(9, null);
            }
            pstmt.setString(10, detail.getIntroductionYear());
            pstmt.setString(11, detail.getDbName());
            pstmt.setString(12, detail.getDbMode());
            pstmt.setString(13, detail.getVerticaVersion());
            pstmt.setString(14, detail.getLicenseInfo());
            pstmt.setString(15, detail.getSaid());
            pstmt.setString(16, detail.getNodeCount());
            pstmt.setString(17, detail.getVerticaAdmin());
            pstmt.setString(18, detail.getSubclusterYn());
            pstmt.setString(19, detail.getMcYn());
            pstmt.setString(20, detail.getMcHost());
            pstmt.setString(21, detail.getMcVersion());
            pstmt.setString(22, detail.getMcAdmin());
            pstmt.setString(23, detail.getBackupYn());
            pstmt.setString(24, detail.getCustomResourcePoolYn());
            pstmt.setString(25, detail.getBackupNote());
            pstmt.setString(26, detail.getOsInfo());
            pstmt.setString(27, detail.getMemoryInfo());
            pstmt.setString(28, detail.getInfraType());
            pstmt.setString(29, detail.getCpuSocket());
            pstmt.setString(30, detail.getHyperThreading());
            pstmt.setString(31, detail.getCpuCore());
            pstmt.setString(32, detail.getDataArea());
            pstmt.setString(33, detail.getDepotArea());
            pstmt.setString(34, detail.getCatalogArea());
            pstmt.setString(35, detail.getObjectArea());
            pstmt.setString(36, detail.getPublicYn());
            pstmt.setString(37, detail.getPublicNetwork());
            pstmt.setString(38, detail.getPrivateYn());
            pstmt.setString(39, detail.getPrivateNetwork());
            pstmt.setString(40, detail.getStorageYn());
            pstmt.setString(41, detail.getStorageNetwork());
            pstmt.setString(42, detail.getEtlTool());
            pstmt.setString(43, detail.getBiTool());
            pstmt.setString(44, detail.getDbEncryption());
            pstmt.setString(45, detail.getCdcTool());
            if (detail.getEosDate() != null) {
                pstmt.setTimestamp(46, new Timestamp(detail.getEosDate().getTime()));
            } else {
                pstmt.setTimestamp(46, null);
            }
            pstmt.setString(47, detail.getCustomerType());
            pstmt.setString(48, detail.getNote());
            pstmt.setString(49, detail.getCustomerName());

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }

 // PreparedStatement ?Œë¼ë¯¸í„° ?¤ì • ë©”ì†Œ??
 // PreparedStatement ?Œë¼ë¯¸í„° ?¤ì • ë©”ì†Œ??(INSERT??
    private void setDetailParameters(PreparedStatement pstmt, CustomerDetailDTO detail) throws SQLException {
        pstmt.setString(1, detail.getCustomerName());        // customer_name
        pstmt.setString(2, detail.getSystemName());          // system_name (ì¶”ê?)
        pstmt.setString(3, detail.getCustomerManager());     // customer_manager
        pstmt.setString(4, detail.getSiCompany());           // si_company
        pstmt.setString(5, detail.getSiManager());           // si_manager
        pstmt.setString(6, detail.getCreator());             // creator

        if (detail.getCreateDate() != null) {
            pstmt.setTimestamp(7, new Timestamp(detail.getCreateDate().getTime()));
        } else {
            pstmt.setTimestamp(7, null);
        }

        pstmt.setString(8, detail.getMainManager());         // main_manager
        pstmt.setString(9, detail.getSubManager());          // sub_manager

        if (detail.getInstallDate() != null) {
            pstmt.setTimestamp(10, new Timestamp(detail.getInstallDate().getTime()));
        } else {
            pstmt.setTimestamp(10, null);
        }

        pstmt.setString(11, detail.getIntroductionYear());   // introduction_year
        pstmt.setString(12, detail.getDbName());             // db_name
        pstmt.setString(13, detail.getDbMode());             // db_mode
        pstmt.setString(14, detail.getVerticaVersion());     // vertica_version
        pstmt.setString(15, detail.getLicenseInfo());        // license_info
        pstmt.setString(16, detail.getSaid());               // said
        pstmt.setString(17, detail.getNodeCount());          // node_count
        pstmt.setString(18, detail.getVerticaAdmin());       // vertica_admin
        pstmt.setString(19, detail.getSubclusterYn());       // subcluster_yn
        pstmt.setString(20, detail.getMcYn());               // mc_yn
        pstmt.setString(21, detail.getMcHost());             // mc_host
        pstmt.setString(22, detail.getMcVersion());          // mc_version
        pstmt.setString(23, detail.getMcAdmin());            // mc_admin
        pstmt.setString(24, detail.getBackupYn());           // backup_yn
        pstmt.setString(25, detail.getCustomResourcePoolYn()); // custom_resource_pool_yn
        pstmt.setString(26, detail.getBackupNote());         // backup_note
        pstmt.setString(27, detail.getOsInfo());             // os_info
        pstmt.setString(28, detail.getMemoryInfo());         // memory_info
        pstmt.setString(29, detail.getInfraType());          // infra_type
        pstmt.setString(30, detail.getCpuSocket());          // cpu_socket
        pstmt.setString(31, detail.getHyperThreading());     // hyper_threading
        pstmt.setString(32, detail.getCpuCore());            // cpu_core
        pstmt.setString(33, detail.getDataArea());           // data_area
        pstmt.setString(34, detail.getDepotArea());          // depot_area
        pstmt.setString(35, detail.getCatalogArea());        // catalog_area
        pstmt.setString(36, detail.getObjectArea());         // object_area
        pstmt.setString(37, detail.getPublicYn());           // public_yn
        pstmt.setString(38, detail.getPublicNetwork());      // public_network
        pstmt.setString(39, detail.getPrivateYn());          // private_yn
        pstmt.setString(40, detail.getPrivateNetwork());     // private_network
        pstmt.setString(41, detail.getStorageYn());          // storage_yn
        pstmt.setString(42, detail.getStorageNetwork());     // storage_network
        pstmt.setString(43, detail.getEtlTool());            // etl_tool
        pstmt.setString(44, detail.getBiTool());             // bi_tool
        pstmt.setString(45, detail.getDbEncryption());       // db_encryption
        pstmt.setString(46, detail.getCdcTool());            // cdc_tool

        if (detail.getEosDate() != null) {
            pstmt.setTimestamp(47, new Timestamp(detail.getEosDate().getTime()));
        } else {
            pstmt.setTimestamp(47, null);
        }

        pstmt.setString(48, detail.getCustomerType());       // customer_type
        pstmt.setString(49, detail.getNote());               // note
    }

    // ê³ ê°???ì„¸?•ë³´ ?? œ
    public boolean deleteCustomerDetail(String customerName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM vertica_customer_detail WHERE customer_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerName);

            int rowsAffected = pstmt.executeUpdate();
            success = (rowsAffected > 0);

        } catch (SQLException  e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(pstmt, conn);
        }

        return success;
    }
}
