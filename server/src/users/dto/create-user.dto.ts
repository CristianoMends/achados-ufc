export class CreateUserDto {
    username: String;
    email: String;
    password: String;
    name: string;
    phone: string | null;
    imageUrl: string | null;
    surname: string | null;
}
