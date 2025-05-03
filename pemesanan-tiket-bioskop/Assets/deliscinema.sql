-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 30, 2025 at 04:37 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `deliscinema`
--

-- --------------------------------------------------------

--
-- Table structure for table `film`
--

CREATE TABLE `film` (
  `id_film` int(11) NOT NULL,
  `judul` varchar(100) DEFAULT NULL,
  `durasi` int(11) DEFAULT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `status` enum('tayang','selesai') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `film`
--

INSERT INTO `film` (`id_film`, `judul`, `durasi`, `genre`, `status`) VALUES
(1, 'Jumbo', 120, 'Comedy', 'tayang');

-- --------------------------------------------------------

--
-- Table structure for table `jadwal`
--

CREATE TABLE `jadwal` (
  `id_jadwal` int(11) NOT NULL,
  `id_film` int(11) DEFAULT NULL,
  `id_studio` int(11) DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `jam` time DEFAULT NULL,
  `harga` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `jadwal`
--

INSERT INTO `jadwal` (`id_jadwal`, `id_film`, `id_studio`, `tanggal`, `jam`, `harga`) VALUES
(1, 1, 2, '2025-05-03', '07:30:00', 50000);

-- --------------------------------------------------------

--
-- Table structure for table `kursi_terpesan`
--

CREATE TABLE `kursi_terpesan` (
  `id_kursi` int(11) NOT NULL,
  `id_transaksi` int(11) DEFAULT NULL,
  `id_jadwal` int(11) DEFAULT NULL,
  `nomor_kursi` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `studio`
--

CREATE TABLE `studio` (
  `id_studio` int(11) NOT NULL,
  `nama_studio` varchar(100) DEFAULT NULL,
  `kapasitas` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `studio`
--

INSERT INTO `studio` (`id_studio`, `nama_studio`, `kapasitas`) VALUES
(1, 'Studio 1', 40),
(2, 'Studio 2', 40),
(3, 'Studio 3', 40),
(4, 'Studio 4', 40);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `id_user` int(11) DEFAULT NULL,
  `id_jadwal` int(11) DEFAULT NULL,
  `waktu_beli` datetime DEFAULT NULL,
  `total_kursi` int(11) DEFAULT NULL,
  `total_bayar` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role` enum('admin','staff') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `username`, `password`, `role`) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin'),
(2, 'staff', '10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6', 'staff');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `film`
--
ALTER TABLE `film`
  ADD PRIMARY KEY (`id_film`);

--
-- Indexes for table `jadwal`
--
ALTER TABLE `jadwal`
  ADD PRIMARY KEY (`id_jadwal`),
  ADD KEY `id_film` (`id_film`),
  ADD KEY `id_studio` (`id_studio`);

--
-- Indexes for table `kursi_terpesan`
--
ALTER TABLE `kursi_terpesan`
  ADD PRIMARY KEY (`id_kursi`),
  ADD KEY `id_transaksi` (`id_transaksi`),
  ADD KEY `id_jadwal` (`id_jadwal`);

--
-- Indexes for table `studio`
--
ALTER TABLE `studio`
  ADD PRIMARY KEY (`id_studio`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_jadwal` (`id_jadwal`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `film`
--
ALTER TABLE `film`
  MODIFY `id_film` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `jadwal`
--
ALTER TABLE `jadwal`
  MODIFY `id_jadwal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `kursi_terpesan`
--
ALTER TABLE `kursi_terpesan`
  MODIFY `id_kursi` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `studio`
--
ALTER TABLE `studio`
  MODIFY `id_studio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `jadwal`
--
ALTER TABLE `jadwal`
  ADD CONSTRAINT `jadwal_ibfk_1` FOREIGN KEY (`id_film`) REFERENCES `film` (`id_film`),
  ADD CONSTRAINT `jadwal_ibfk_2` FOREIGN KEY (`id_studio`) REFERENCES `studio` (`id_studio`);

--
-- Constraints for table `kursi_terpesan`
--
ALTER TABLE `kursi_terpesan`
  ADD CONSTRAINT `kursi_terpesan_ibfk_1` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id_transaksi`),
  ADD CONSTRAINT `kursi_terpesan_ibfk_2` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal` (`id_jadwal`);

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
  ADD CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal` (`id_jadwal`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
