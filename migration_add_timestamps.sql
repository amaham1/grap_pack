-- Migration: Add missing timestamp columns to content table
-- Date: 2025-11-08
-- Issue: SQLSyntaxErrorException - Unknown column 'c.created_at' in 'field list'

-- Add created_at and updated_at columns to content table if they don't exist
ALTER TABLE content
ADD COLUMN IF NOT EXISTS created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Verify the columns were added
DESCRIBE content;
