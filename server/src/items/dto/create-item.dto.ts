import { IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateItemDto {
    @IsString()
    @IsNotEmpty()
    title: string;

    @IsString()
    @IsNotEmpty()
    description: string;

    @IsString()
    @IsNotEmpty()
    location: string;

    @IsBoolean()
    @IsOptional()
    isFound?: boolean;
}
