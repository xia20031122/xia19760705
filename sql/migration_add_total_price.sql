-- Migration: Add total_price column to ape_vegetable_order table
-- This migration adds a total_price column and migrates data from the old price column

-- Add total_price column
ALTER TABLE `ape_vegetable_order` ADD COLUMN `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价' AFTER `price`;

-- Migrate existing data: move current price (which was total) to total_price
-- and calculate unit price by dividing by quantity
UPDATE `ape_vegetable_order` 
SET 
    `total_price` = `price`,
    `price` = CASE 
        WHEN `num` > 0 THEN `price` / `num`
        ELSE `price`
    END
WHERE `total_price` IS NULL;

-- Update price column comment to reflect it's now unit price
ALTER TABLE `ape_vegetable_order` MODIFY COLUMN `price` decimal(10,2) DEFAULT NULL COMMENT '单价';
