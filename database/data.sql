-- ============================================================
-- 旅游管理系统 - 初始化数据
-- ============================================================

-- ===================== 用户 =====================
-- 密码均为 BCrypt 加密（Spring Security BCryptPasswordEncoder 生成）
-- admin  → admin123
-- user01 → user123
-- user02 → user123

INSERT INTO sys_user (username, password_hash, nickname, phone, email, role, status) VALUES
('admin',  '$2a$10$nhDKy4cAMoylQ5rVFB5I2.SHXfpEFxX3QvcI4TyHTxJNQs4VESvvq', '管理员',   '13800000000', 'admin@example.com',  'ADMIN', 'ENABLED'),
('user01', '$2a$10$4888Mud8sA.0sBNbmxWePuC3zURfTt2pJW4WEBsZh/dxz1yq1k2oG', '张三',     '13800000001', 'user01@example.com', 'USER',  'ENABLED'),
('user02', '$2a$10$4888Mud8sA.0sBNbmxWePuC3zURfTt2pJW4WEBsZh/dxz1yq1k2oG', '李四',     '13800000002', 'user02@example.com', 'USER',  'ENABLED');

-- ===================== 景点分类 =====================

INSERT INTO spot_category (name, sort_order, status) VALUES
('自然风光', 1, 'ON'),
('人文古迹', 2, 'ON'),
('主题乐园', 3, 'ON'),
('休闲度假', 4, 'ON');

-- ===================== 景点 =====================

INSERT INTO scenic_spot (name, category_id, address, ticket_price, open_time, introduction, image_url, status) VALUES
('青山湖风景区',     1, '某市青山湖路1号',       60.00,  '08:00-18:00', '以湖泊和山地景观为主的休闲旅游景区。',                 '/images/spot-qingshan.jpg',    'ON'),
('碧海沙滩',         1, '某市滨海大道88号',       80.00,  '09:00-19:00', '拥有细沙海滩和清澈海水的滨海度假胜地。',               '/images/spot-bihai.jpg',       'ON'),
('古城墙遗址公园',   2, '某市中心古城路10号',     45.00,  '08:30-17:30', '保存完好的明代古城墙，展示城市历史变迁。',             '/images/spot-gucheng.jpg',     'ON'),
('千年古寺',         2, '某市西山风景区内',       30.00,  '07:00-18:00', '始建于唐代的千年古刹，香火旺盛。',                     '/images/spot-gusi.jpg',        'ON'),
('欢乐世界主题乐园', 3, '某市新区欢乐大道1号',   220.00, '09:30-21:00', '集游乐设施、演艺表演于一体的大型主题乐园。',           '/images/spot-huanle.jpg',      'ON'),
('海洋馆',           3, '某市滨海路66号',         150.00, '09:00-18:00', '展示海洋生物的综合性水族馆。',                         '/images/spot-haiyang.jpg',     'ON'),
('温泉度假村',       4, '某市郊区温泉镇',         188.00, '10:00-22:00', '天然温泉与休闲养生相结合的度假胜地。',                 '/images/spot-wenquan.jpg',     'ON'),
('森林公园',         1, '某市北部山区',           40.00,  '06:00-18:00', '植被茂密、空气清新的天然氧吧。',                       '/images/spot-senlin.jpg',      'ON'),
('民俗文化村',       2, '某县民俗路1号',          50.00,  '08:00-17:00', '展示当地民俗文化和传统手工艺的村落景区。',             '/images/spot-minsu.jpg',       'ON'),
('湖畔露营地',       4, '某市东湖公园内',          0.00,  '全天开放',    '适合家庭和朋友的湖畔露营休闲场所。',                   '/images/spot-luying.jpg',      'OFF');

-- ===================== 旅游线路 =====================

INSERT INTO tour_route (name, itinerary, price, departure_time, quota, booked_count, status, cover_image_url) VALUES
('青山湖一日游',       '08:00 集合出发；10:00 游览青山湖；12:00 午餐；14:00 湖畔徒步；16:00 返回。',                  199.00,  '2026-07-01 08:00:00', 30,  5,  'OPEN',   '/images/route-qingshan.jpg'),
('古城文化两日游',     'Day1: 古城墙遗址公园 + 千年古寺；Day2: 民俗文化村 + 自由活动。',                               399.00,  '2026-07-05 08:00:00', 25,  12, 'OPEN',   '/images/route-gucheng.jpg'),
('欢乐世界亲子一日游', '09:00 集合；10:00 入园欢乐世界；12:00 园内午餐；17:00 返回。',                                  299.00,  '2026-07-10 09:00:00', 40,  40, 'FULL',   '/images/route-huanle.jpg'),
('温泉养生两日游',     'Day1: 温泉度假村入住 + 温泉体验；Day2: 森林公园漫步 + 返程。',                                 588.00,  '2026-07-15 08:00:00', 20,  3,  'OPEN',   '/images/route-wenquan.jpg'),
('碧海沙滩周末游',     '周六 08:00 出发；10:00 抵达碧海沙滩；自由活动；周日 15:00 返回。',                              358.00,  '2026-07-20 08:00:00', 35,  0,  'DRAFT',  '/images/route-bihai.jpg'),
('全景三日游',         'Day1: 青山湖 + 森林公园；Day2: 古城墙 + 千年古寺；Day3: 温泉度假村。',                         888.00,  '2026-08-01 08:00:00', 15,  0,  'CLOSED', '/images/route-quanjing.jpg');

-- ===================== 订单 =====================
-- booked_count = PENDING + CONFIRMED 的 people_count 之和
-- CANCELLED / REJECTED / COMPLETED 不计入 booked_count（名额已释放）

INSERT INTO tour_order (order_no, user_id, route_id, people_count, contact_name, contact_phone, total_amount, status, remark, created_at) VALUES
-- 青山湖一日游 (route_id=1, price=199, booked_count=5 → PENDING 2 + CONFIRMED 3)
('TR202606180001', 2, 1, 2, '张三', '13800000001',  398.00, 'PENDING',   NULL,              '2026-06-18 10:00:00'),
('TR202606160001', 3, 1, 3, '李四', '13800000002',  597.00, 'CONFIRMED', NULL,              '2026-06-16 14:00:00'),
('TR202606120001', 3, 1, 2, '李四', '13800000002',  398.00, 'CANCELLED', '临时有事，取消预订', '2026-06-12 09:00:00'),
-- 古城文化两日游 (route_id=2, price=399, booked_count=12 → PENDING 4 + CONFIRMED 8)
('TR202606200001', 3, 2, 4, '李四', '13800000002', 1596.00, 'PENDING',   NULL,              '2026-06-20 11:00:00'),
('TR202606140001', 2, 2, 5, '张三', '13800000001', 1995.00, 'CONFIRMED', NULL,              '2026-06-14 09:00:00'),
('TR202606100001', 2, 2, 3, '张三', '13800000001', 1197.00, 'CONFIRMED', NULL,              '2026-06-10 16:00:00'),
('TR202606080001', 3, 2, 2, '李四', '13800000002',  798.00, 'REJECTED',  '该线路临时调整，暂不接受预订', '2026-06-08 15:00:00'),
-- 欢乐世界亲子一日游 (route_id=3, price=299, booked_count=40 → CONFIRMED 40)
('TR202606050001', 2, 3, 15, '张三', '13800000001', 4485.00, 'CONFIRMED', NULL,             '2026-06-05 08:00:00'),
('TR202606060001', 3, 3, 25, '李四', '13800000002', 7475.00, 'CONFIRMED', NULL,             '2026-06-06 09:00:00'),
-- 温泉养生两日游 (route_id=4, price=588, booked_count=3 → PENDING 3)
('TR202606250001', 2, 4, 3, '张三', '13800000001', 1764.00, 'PENDING',   NULL,              '2026-06-25 10:00:00'),
-- 额外演示数据：已完成订单
('TR202605200001', 3, 2, 4, '李四', '13800000002', 1596.00, 'COMPLETED', '行程顺利，已完结', '2026-05-20 08:00:00');

-- ===================== 公告 =====================

INSERT INTO announcement (title, content, status, publish_time) VALUES
('暑期旅游线路火热预订中',       '暑假来临，多条精选线路已开放预订，名额有限，先到先得！',           'PUBLISHED', '2026-06-15 10:00:00'),
('欢乐世界主题乐园新增夜场',     '即日起欢乐世界主题乐园开放夜场营业至21:00，欢迎体验。',            'PUBLISHED', '2026-06-20 09:00:00'),
('温泉度假村设施升级公告',       '温泉度假村新增室外温泉池和SPA区域，预计7月中旬完工。',             'DRAFT',     NULL);
