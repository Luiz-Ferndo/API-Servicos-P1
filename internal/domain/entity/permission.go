package entity

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
	"gorm.io/gorm"
)

// Permission representa uma permissão no sistema
type Permission struct {
	ID          uint              `gorm:"primaryKey;column:permission_id" json:"id"`
	Name        enum.Permission   `gorm:"unique;not null;column:nm_permission" json:"name"`
	Description string            `gorm:"not null;column:ds_permission" json:"description"`
	Roles       []*Role           `gorm:"many2many:role_permission;foreignKey:ID;joinForeignKey:permission_id;References:ID;joinReferences:cd_role" json:"-"`
}

// TableName especifica o nome da tabela
func (Permission) TableName() string {
	return "permission"
}

// BeforeCreate hook para inicializar campos
func (p *Permission) BeforeCreate(tx *gorm.DB) error {
	if p.Description == "" {
		p.Description = p.Name.GetDescription()
	}
	return nil
}
