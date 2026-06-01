-- ============================================================
-- 旅游管理系统 - 数据库建表脚本
-- 数据库：MySQL 8.0
-- 字符集：utf8mb4
-- ============================================================

-- 1. 用户表
CREATE TABLE sys_user (
  id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(50)  NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  nickname      VARCHAR(50),
  phone         VARCHAR(20),
  email         VARCHAR(100),
  avatar_url    VARCHAR(255),
  role          VARCHAR(20)  NOT NULL DEFAULT 'USER'   COMMENT 'USER / ADMIN',
  status        VARCHAR(20)  NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED / DISABLED',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 景点分类表
CREATE TABLE spot_category (
  id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(50)  NOT NULL,
  sort_order    INT          NOT NULL DEFAULT 0,
  status        VARCHAR(20)  NOT NULL DEFAULT 'ON' COMMENT 'ON / OFF',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_spot_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 景点表
CREATE TABLE scenic_spot (
  id            BIGINT        PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(100)  NOT NULL,
  category_id   BIGINT,
  address       VARCHAR(255),
  ticket_price  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  open_time     VARCHAR(100),
  introduction  TEXT,
  image_url     VARCHAR(255),
  status        VARCHAR(20)   NOT NULL DEFAULT 'ON' COMMENT 'ON / OFF',
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_scenic_spot_name (name),
  KEY idx_scenic_spot_category (category_id),
  CONSTRAINT fk_spot_category FOREIGN KEY (category_id) REFERENCES spot_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 旅游线路表
CREATE TABLE tour_route (
  id              BIGINT        PRIMARY KEY AUTO_INCREMENT,
  name            VARCHAR(100)  NOT NULL,
  itinerary       TEXT          NOT NULL,
  price           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  departure_time  DATETIME      NOT NULL,
  quota           INT           NOT NULL,
  booked_count    INT           NOT NULL DEFAULT 0,
  status          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / OPEN / FULL / CLOSED',
  cover_image_url VARCHAR(255),
  created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_tour_route_name (name),
  KEY idx_tour_route_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 订单表
CREATE TABLE tour_order (
  id             BIGINT        PRIMARY KEY AUTO_INCREMENT,
  order_no       VARCHAR(32)   NOT NULL,
  user_id        BIGINT        NOT NULL,
  route_id       BIGINT        NOT NULL,
  people_count   INT           NOT NULL,
  contact_name   VARCHAR(50)   NOT NULL,
  contact_phone  VARCHAR(20)   NOT NULL,
  total_amount   DECIMAL(10,2) NOT NULL,
  status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / CONFIRMED / CANCELLED / REJECTED / COMPLETED',
  remark         VARCHAR(255),
  created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_tour_order_no (order_no),
  KEY idx_tour_order_user (user_id),
  KEY idx_tour_order_route (route_id),
  KEY idx_tour_order_status (status),
  CONSTRAINT fk_order_user  FOREIGN KEY (user_id)  REFERENCES sys_user(id),
  CONSTRAINT fk_order_route FOREIGN KEY (route_id) REFERENCES tour_route(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 评论表（避免 comment 关键字冲突，命名为 travel_comment）
CREATE TABLE travel_comment (
  id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
  user_id       BIGINT       NOT NULL,
  target_type   VARCHAR(20)  NOT NULL COMMENT 'SPOT / ROUTE',
  target_id     BIGINT       NOT NULL,
  rating        INT          NOT NULL DEFAULT 5,
  content       VARCHAR(500) NOT NULL,
  status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / APPROVED / REJECTED',
  audit_remark  VARCHAR(255),
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_comment_user (user_id),
  KEY idx_comment_target (target_type, target_id),
  KEY idx_comment_status (status),
  CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 收藏表
CREATE TABLE favorite (
  id          BIGINT   PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT   NOT NULL,
  target_type VARCHAR(20) NOT NULL COMMENT 'SPOT / ROUTE',
  target_id   BIGINT   NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_favorite_user_target (user_id, target_type, target_id),
  KEY idx_favorite_user (user_id),
  CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 公告表
CREATE TABLE announcement (
  id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
  title        VARCHAR(100) NOT NULL,
  content      TEXT         NOT NULL,
  status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / PUBLISHED / OFFLINE',
  publish_time DATETIME,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_announcement_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
